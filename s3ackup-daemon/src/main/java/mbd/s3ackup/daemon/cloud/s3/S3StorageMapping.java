package mbd.s3ackup.daemon.cloud.s3;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.s3.model.StorageClass;

import mbd.s3ackup.daemon.cloud.CloudFile.StorageType;

public class S3StorageMapping {

	private static Map<StorageType, StorageClass> storageTypeMap = new HashMap<>();
	private static Map<StorageClass, StorageType> storageClassMap = new HashMap<>();

	public static StorageClass mapStorageTypeToAwsStorageClass(StorageType storageType) {
		return storageTypeMap.get(storageType);
	}

	public static StorageType mapAwsStorageClassToStorageType(StorageClass storageClass) {
		return storageClassMap.get(storageClass);
	}

	public static StorageType mapAwsStorageClassToStorageType(String storageClass) {
		return mapAwsStorageClassToStorageType(StorageClass.fromValue(storageClass));
	}

	private static void init(StorageType storageType, StorageClass storageClass) {
		storageTypeMap.put(storageType, storageClass);
		storageClassMap.put(storageClass, storageType);
	}

	static {
		init(StorageType.STANDARD, StorageClass.Standard);
		init(StorageType.STANDARD_IA, StorageClass.StandardInfrequentAccess);
		init(StorageType.REDUCED_REDUNDANCY, StorageClass.ReducedRedundancy);
		init(StorageType.GLACIER, StorageClass.Glacier);
	}
}
