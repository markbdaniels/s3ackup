package mbd.s3ackup.daemon.cloud.s3;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.machinezoo.noexception.Exceptions;

import mbd.s3ackup.TestsAppCtxConfig;
import mbd.s3ackup.daemon.cloud.CloudDirectory;
import mbd.s3ackup.daemon.cloud.CloudPath;
import mbd.s3ackup.daemon.cloud.CloudRoot;
import mbd.s3ackup.daemon.cloud.CloudStorage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestsAppCtxConfig.class)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class S3UploadTest {

	private static final Logger log = LoggerFactory.getLogger(S3UploadTest.class);

	@Autowired
	private CloudStorage s3Upload;

	@Autowired
	private S3Client s3Client;

	@Value("${aws.bucket}")
	private String bucket;

	@Test
	public void testListRoots() {
		List<CloudRoot> listRoots = s3Upload.listRootDirectories();
		assertTrue(listRoots.size() > 0);
		assertTrue(listRoots.contains(new CloudRoot(bucket)));
		listRoots.forEach(i -> log.info("{}", i));
	}

	@Test
	public void test_listFiles() {
		// empty prefix
		List<CloudPath> listChildren = s3Upload.listFiles(new CloudDirectory(new CloudRoot(bucket), ""));
		assertEquals(4, listChildren.size());
		listChildren.forEach(i -> log.info("prefix[''] {}", i));
		assertEquals("a/", listChildren.get(0).getPath());
		assertEquals("b/", listChildren.get(1).getPath());
		assertEquals("c/", listChildren.get(2).getPath());
		assertEquals("1.txt", listChildren.get(3).getPath());

		// a prefix
		listChildren = s3Upload.listFiles(new CloudDirectory(new CloudRoot(bucket), "a/"));
		assertEquals(1, listChildren.size());
		listChildren.forEach(i -> log.info("prefix[a/] {}", i));
		assertEquals("a/2.txt", listChildren.get(0).getPath());

		// b prefix
		listChildren = s3Upload.listFiles(new CloudDirectory(new CloudRoot(bucket), "b/"));
		assertEquals(1, listChildren.size());
		listChildren.forEach(i -> log.info("prefix[b/] {}", i));
		assertEquals("b/3.txt", listChildren.get(0).getPath());

		// c prefix
		listChildren = s3Upload.listFiles(new CloudDirectory(new CloudRoot(bucket), "c/"));
		assertEquals(1, listChildren.size());
		listChildren.forEach(i -> log.info("prefix[c/] {}", i));
		assertEquals("c/d/", listChildren.get(0).getPath());

		// c/d prefix
		listChildren = s3Upload.listFiles(new CloudDirectory(new CloudRoot(bucket), "c/d/"));
		assertEquals(2, listChildren.size());
		listChildren.forEach(i -> log.info("prefix[c/d/] {}", i));
		assertEquals("c/d/4.txt", listChildren.get(0).getPath());
		assertEquals("c/d/5.txt", listChildren.get(1).getPath());
	}

	@Test
	public void test_listFilesRecursive() {
		// empty prefix
		List<CloudPath> listChildren = s3Upload.listFilesRecursive(new CloudDirectory(new CloudRoot(bucket), ""));
		assertEquals(5, listChildren.size());
		listChildren.forEach(i -> log.info("{}", i));

		// a prefix
		listChildren = s3Upload.listFilesRecursive(new CloudDirectory(new CloudRoot(bucket), "a"));
		assertEquals(1, listChildren.size());
		listChildren.forEach(i -> log.info("{}", i));

		// c prefix
		listChildren = s3Upload.listFilesRecursive(new CloudDirectory(new CloudRoot(bucket), "c"));
		assertEquals(2, listChildren.size());
		listChildren.forEach(i -> log.info("{}", i));

		// c/d prefix
		listChildren = s3Upload.listFilesRecursive(new CloudDirectory(new CloudRoot(bucket), "c/d"));
		assertEquals(2, listChildren.size());
		listChildren.forEach(i -> log.info("{}", i));

		// c/d/4.txt prefix
		listChildren = s3Upload.listFilesRecursive(new CloudDirectory(new CloudRoot(bucket), "c/d/4.txt"));
		assertEquals(1, listChildren.size());
		listChildren.forEach(i -> log.info("{}", i));
	}

	@Test
	public void test_listDirectoriesRecursive() {
		// empty prefix
		List<CloudDirectory> dirs = s3Upload.listDirectoriesRecursive(new CloudDirectory(new CloudRoot(bucket), ""));
		assertEquals(5, dirs.size());
		dirs.forEach(i -> log.info("{}", i));

		// a prefix
		dirs = s3Upload.listDirectoriesRecursive(new CloudDirectory(new CloudRoot(bucket), "a"));
		assertEquals(1, dirs.size());
		dirs.forEach(i -> log.info("{}", i));

		// c prefix
		dirs = s3Upload.listDirectoriesRecursive(new CloudDirectory(new CloudRoot(bucket), "c"));
		assertEquals(2, dirs.size());
		dirs.forEach(i -> log.info("{}", i));

		// c/d prefix
		dirs = s3Upload.listDirectoriesRecursive(new CloudDirectory(new CloudRoot(bucket), "c/d"));
		assertEquals(2, dirs.size());
		dirs.forEach(i -> log.info("{}", i));
	}

	@Test
	public void testSaveFile() throws IOException {
		Path tmpFile = createTmpFile("newfile.txt");
		s3Upload.uploadFile(tmpFile, new CloudDirectory(new CloudRoot(bucket), "new/newfile.txt"));

		List<CloudPath> listChildren = s3Upload.listFiles(new CloudDirectory(new CloudRoot(bucket), "new/"));
		assertEquals(1, listChildren.size());
		listChildren.forEach(i -> log.info("{}", i));
	}

	/**
	 * Prepare a test bucket with objects
	 * 
	 * @throws IOException
	 */
	@Before
	public void init() throws IOException {
		// just in case - lets make sure we not truncating a non test bucket
		if (!bucket.contains("test")) {
			throw new IllegalArgumentException("Expected 'test' to be in the bucket name[" + bucket + "]");
		}

		log.info("### Setting up data in S3 for integration tests ###");
		log.info("### deleting all objects in bucket[{}] ###", bucket);
		ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(bucket);
		ListObjectsV2Result listObjectsV2 = null;
		do {
			listObjectsV2 = s3Client.getS3Client().listObjectsV2(listObjectsV2Request);
			listObjectsV2.getObjectSummaries().parallelStream().forEach(item -> {
				log.info("deleting:[{}][{}]", item.getBucketName(), item.getKey());
				s3Client.getS3Client().deleteObject(item.getBucketName(), item.getKey());
			});
			listObjectsV2Request.setContinuationToken(listObjectsV2.getNextContinuationToken());
		} while (listObjectsV2.isTruncated());

		log.info("### creating files in bucket[{}] ###", bucket);
		List<String> fileList = new ArrayList<>();
		fileList.add("1.txt");
		fileList.add("a/2.txt");
		fileList.add("b/3.txt");
		fileList.add("c/d/4.txt");
		fileList.add("c/d/5.txt");
		fileList.parallelStream().forEach(Exceptions.sneak().consumer(file -> {
			Path tmpFile = createTmpFile(file);
			s3Client.getS3Client().putObject(bucket, file, tmpFile.toFile());
			log.info("uploading file to: [{}/{}]", bucket, file);
		}));
	}

	private Path createTmpFile(String file) throws IOException {
		Path tmpDirPath = Paths.get(System.getProperty("java.io.tmpdir"), "s3ackup-daemon", "test", "integration", bucket);
		Path fullPath = tmpDirPath.resolve(file);
		Files.deleteIfExists(fullPath);
		Files.createDirectories(fullPath.getParent());

		OutputStream out = Files.newOutputStream(fullPath, CREATE, TRUNCATE_EXISTING);
		out.write("Hello World".getBytes());
		out.flush();
		out.close();

		return fullPath;
	}
}
