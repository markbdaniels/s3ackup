package mbd.s3ackup.daemon.cloud.s3;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectId;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import mbd.s3ackup.daemon.cloud.CloudDirectory;
import mbd.s3ackup.daemon.cloud.CloudDirectoryAttributes;
import mbd.s3ackup.daemon.cloud.CloudFile;
import mbd.s3ackup.daemon.cloud.CloudFile.StorageType;
import mbd.s3ackup.daemon.local.PathUtils;
import mbd.s3ackup.daemon.cloud.CloudPath;
import mbd.s3ackup.daemon.cloud.CloudRoot;
import mbd.s3ackup.daemon.cloud.CloudStorage;
import mbd.s3ackup.daemon.cloud.NotCalculatableException;

@Service
public class S3Upload implements CloudStorage {

	private static final Logger log = LoggerFactory.getLogger(S3Upload.class);

	@Autowired
	private S3Client s3Client;

	@Override
	@Cacheable(value = "default")
	public List<CloudRoot> listRootDirectories() {
		log.info("s3Client.getS3Client().listBuckets()");
		List<Bucket> listBuckets = s3Client.getS3Client().listBuckets();
		List<CloudRoot> collect = listBuckets.stream().map(S3Upload::mapToCloudRoot).collect(Collectors.toList());
		return collect;
	}

	@Override
	public List<CloudPath> listFiles(CloudDirectory prefix) {
		String bucketName = prefix.getRoot().getName();
		String t = prefix.getPath();
		String prefixString = "".equals(t) || t.charAt(t.length() - 1) == '/' ? t : t + "/";

		long counter = 0;
		Set<CloudPath> collect = new HashSet<>();
		ListObjectsV2Result listObjectsV2 = null;
		ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(bucketName)
				.withPrefix(prefixString).withDelimiter("/");
		do {
			listObjectsV2 = s3Client.getS3Client().listObjectsV2(listObjectsV2Request);

			// directories
			for (String dir : listObjectsV2.getCommonPrefixes()) {
				collect.add(new CloudDirectory(new CloudRoot(bucketName), dir));
				counter++;
			}

			// files
			for (S3ObjectSummary s : listObjectsV2.getObjectSummaries()) {
				collect.add(S3Upload.mapToCloudFile(s));
				counter++;
			}
			listObjectsV2Request.setContinuationToken(listObjectsV2.getNextContinuationToken());
		}
		while (listObjectsV2.isTruncated());

		log.info("s3Client.getS3Client().listObjectsV2 [s3://{}/{}] [{}]", bucketName, prefixString, counter);
		return collect.stream().sorted(CloudPath.sortByTypeThenNameComparator()).collect(Collectors.toList());
	}

	@Override
	public List<CloudPath> listFilesRecursive(CloudDirectory prefix) {
		Set<CloudPath> collect = new TreeSet<>();
		Consumer<S3ObjectSummary> consumer = item -> {
			collect.add(S3Upload.mapToCloudFile(item));
		};
		processS3ListWithConsumer(prefix, consumer, 0);
		return new ArrayList<>(collect);
	}

	@Override
	public List<CloudDirectory> listDirectoriesRecursive(CloudDirectory prefix) {
		Set<CloudDirectory> dirSet = new TreeSet<>();
		Consumer<S3ObjectSummary> consumer = item -> {
			String s = item.getKey();
			Path objectPath = Paths.get(s);
			Path pathWithoutFile = objectPath.getParent();
			do {
				dirSet.add(new CloudDirectory(prefix.getRoot(), pathWithoutFile));
				pathWithoutFile = pathWithoutFile == null ? null : pathWithoutFile.getParent();
			}
			while (pathWithoutFile != null && pathWithoutFile.getNameCount() > 0);
		};
		processS3ListWithConsumer(prefix, consumer, 0);
		return new ArrayList<>(dirSet);
	}

	@Override
	public void uploadFile(Path localPath, CloudPath cloudFile) {
		// first check whether the file does not already exist in s3 - this will
		// save us from uploading big files multiple times.
		String localMd5sum = PathUtils.calculateMd5hash(localPath);
		List<CloudPath> remoteFileList = listFilesRecursive(
				new CloudDirectory(cloudFile.getRoot(), cloudFile.getPath()));
		if (remoteFileList.size() == 1) {
			CloudPath cloudPath = remoteFileList.get(0);
			if (cloudPath instanceof CloudFile) {
				String remoteMd5sum = ((CloudFile) cloudPath).getHash();
				if (StringUtils.isNotEmpty(localMd5sum) && localMd5sum.equals(remoteMd5sum)) {
					log.warn("file [{}][{}] has same md5sum as remote file - ignoring upload", localPath, localMd5sum);
					return;
				}
			}
		}

		for (int i = 0; i < 3; i++) {
			try {
				s3Client.getS3Client().putObject(cloudFile.getRoot().getName(), cloudFile.getPath(),
						localPath.toFile());
				log.info("put:[{}] to [s3://{}/{}]", localPath, cloudFile.getRoot().getName(), cloudFile.getPath());
				return;
			}
			catch (AmazonS3Exception e) {
				log.warn("error uploading file[{}] - trying [{}] more times. [{}]", localPath, (2 - i), e.getMessage());
			}
		}
		log.warn("unable to upload file[{}] to [s3://{}/{}]", localPath, cloudFile.getRoot().getName(),
				cloudFile.getPath());
	}

	@Override
	public void deleteFile(CloudPath cloudFile) {
		s3Client.getS3Client().deleteObject(cloudFile.getRoot().getName(), cloudFile.getPath());
		log.info("delete: [s3://{}/{}]", cloudFile.getRoot().getName(), cloudFile.getPath());
	}

	private boolean processS3ListWithConsumer(CloudPath fullPath, Consumer<S3ObjectSummary> c, long limit) {
		String bucketName = fullPath.getRoot().getName();
		String prefix = fullPath.getPath();

		ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(bucketName)
				.withPrefix(prefix);

		long counter = 0;
		ListObjectsV2Result listObjectsV2 = null;
		boolean success = true;

		outter: do {
			listObjectsV2 = s3Client.getS3Client().listObjectsV2(listObjectsV2Request);
			for (S3ObjectSummary s : listObjectsV2.getObjectSummaries()) {
				counter++;
				if (limit > 0 && counter > limit) {
					success = false;
					break outter;
				}
				c.accept(s);
			}
			listObjectsV2Request.setContinuationToken(listObjectsV2.getNextContinuationToken());
		}
		while (listObjectsV2.isTruncated());

		log.info("s3Client.getS3Client().listObjectsV2 [s3://{}/{}] [{}]", bucketName, prefix, counter);
		return success;
	}

	private static CloudRoot mapToCloudRoot(Bucket bucket) {
		CloudRoot out = new CloudRoot(bucket.getName(), bucket.getCreationDate());
		return out;
	}

	private static CloudFile mapToCloudFile(S3ObjectSummary item) {
		CloudFile out = new CloudFile(new CloudRoot(item.getBucketName()), item.getKey());
		out.setHash(item.getETag());
		out.setLastModified(item.getLastModified());
		out.setSize(item.getSize());
		out.setStorageType(S3StorageMapping.mapAwsStorageClassToStorageType(item.getStorageClass()));
		return out;
	}

	private static final int MAX_OBJECTS_FOR_DIR_STATS = 1_000;

	@Override
	public CloudDirectoryAttributes getDirectoryAttributes(CloudDirectory prefix, boolean forceCalculatable)
			throws NotCalculatableException {
		CloudDirectoryAttributes out = new CloudDirectoryAttributes();
		Consumer<S3ObjectSummary> consumer = item -> {
			StorageType storageType = S3StorageMapping.mapAwsStorageClassToStorageType(item.getStorageClass());
			out.incrementStats(storageType, item.getSize(), item.getLastModified());
		};
		boolean processedFullDataset = processS3ListWithConsumer(prefix, consumer,
				forceCalculatable ? 0 : MAX_OBJECTS_FOR_DIR_STATS);
		if (!processedFullDataset) {
			throw new NotCalculatableException(String.format("objects > %s", MAX_OBJECTS_FOR_DIR_STATS));
		}
		return out;
	}

	@Override
	public void downloadFile(Path localPath, CloudFile cloudfile) {
		GetObjectRequest getObjectRequest = new GetObjectRequest(
				new S3ObjectId(cloudfile.getRoot().getName(), cloudfile.getPath()));
		ObjectMetadata response = s3Client.getS3Client().getObject(getObjectRequest, localPath.toFile());
		if (response == null) {
			log.warn("unable to get: [s3://{}/{}] to [{}]", cloudfile.getRoot().getName(), cloudfile.getPath(),
					localPath);
		}
		else {
			log.info("get: [s3://{}/{}] to [{}]", cloudfile.getRoot().getName(), cloudfile.getPath(), localPath);
		}
	}

	@Override
	public void changeStorageClass(CloudFile cloudfile, StorageType newStorageType) {
		Consumer<S3ObjectSummary> consumer = item -> {
			StorageType oldStorageType = S3StorageMapping.mapAwsStorageClassToStorageType(item.getStorageClass());
			if (!oldStorageType.equals(newStorageType)) {
				String bucketName = item.getBucketName();
				String key = item.getKey();
				CopyObjectRequest req = new CopyObjectRequest(bucketName, key, bucketName, key)
						.withStorageClass(S3StorageMapping.mapStorageTypeToAwsStorageClass(newStorageType));
				s3Client.getS3Client().copyObject(req);
			}
		};
		processS3ListWithConsumer(cloudfile, consumer, 1);
	}
}
