package mbd.s3ackup.daemon.api.rest;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import mbd.s3ackup.daemon.api.rest.dto.FileStatsResponse;
import mbd.s3ackup.daemon.api.rest.dto.ListFileResponse;
import mbd.s3ackup.daemon.api.rest.dto.ListRootDirectoriesResponse;
import mbd.s3ackup.daemon.api.rest.dto.ListTasksResponse;
import mbd.s3ackup.daemon.cloud.s3.S3Client.NotAuthenticatedException;
import mbd.s3ackup.daemon.util.DateUtil;

@RestController
@RequestMapping(value = "/api")
public class RestApiController {

	private static final Logger log = LoggerFactory.getLogger(RestApiController.class);

	@Autowired
	private RestApiService apiService;

	@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "FORBIDDEN")
	@ExceptionHandler({ NotAuthenticatedException.class })
	public void handleException() {
		//
	}

	/**
	 * List root directories
	 */
	private static final String GET_ROOT_DIRECTORIES = "/getRootDirectories";

	@RequestMapping(value = GET_ROOT_DIRECTORIES, method = RequestMethod.GET)
	public ListRootDirectoriesResponse getRootDirectories() throws ParseException {
		long t = System.currentTimeMillis();
		ListRootDirectoriesResponse response = apiService.getRootDirectories();
		log.debug("[{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), GET_ROOT_DIRECTORIES, response);
		return response;
	}

	/**
	 * List directories/files under a path
	 */
	private static final String GET_FILE_LIST = "/getFileList";

	@RequestMapping(value = GET_FILE_LIST, method = RequestMethod.GET)
	public ListFileResponse getFileList(@RequestParam(name = "path", required = true) String path,
			@RequestParam(name = "refresh", required = false, defaultValue = "false") boolean refresh)
			throws ParseException {
		long t = System.currentTimeMillis();
		ListFileResponse response = apiService.getFileList(path, refresh);
		log.debug("[{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), GET_ROOT_DIRECTORIES, response);
		return response;
	}

	/**
	 * Enables a root directory (which creates the child directories and adds a watcher
	 * service to the directories)
	 */
	private static final String POST_ENABLE_ROOT_DIRECTORY = "/enableRootDirectory";

	@RequestMapping(value = POST_ENABLE_ROOT_DIRECTORY, method = RequestMethod.POST)
	public ListRootDirectoriesResponse enableRootDirectory(
			@RequestParam(name = "root", required = false) Set<String> rootDirectorySet) throws ParseException {
		long t = System.currentTimeMillis();
		ListRootDirectoriesResponse response = apiService.enableRootDirectory(rootDirectorySet);
		log.debug("[{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), POST_ENABLE_ROOT_DIRECTORY, response);
		return response;
	}

	/**
	 * Disables a root directory (and removes any watcher service from the directories)
	 */
	private static final String POST_DISABLE_ROOT_DIRECTORY = "/disableRootDirectory";

	@RequestMapping(value = POST_DISABLE_ROOT_DIRECTORY, method = RequestMethod.POST)
	public ListRootDirectoriesResponse disableRootDirectory(
			@RequestParam(name = "root", required = false) Set<String> rootDirectorySet) throws ParseException {
		long t = System.currentTimeMillis();
		ListRootDirectoriesResponse response = apiService.disableRootDirectory(rootDirectorySet);
		log.debug("[{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), POST_DISABLE_ROOT_DIRECTORY, response);
		return response;
	}

	/**
	 * Get stats for a path. I.e. size, file count etc
	 */
	private static final String GET_DIRECTORY_STATS = "/getDirectoryStats";

	@RequestMapping(value = GET_DIRECTORY_STATS, method = RequestMethod.GET)
	public FileStatsResponse getPathStats(@RequestParam(name = "path", required = true) String path,
			@RequestParam(name = "forceCalculatable", required = false, defaultValue = "false") boolean forceCalculatable)
			throws ParseException {
		long t = System.currentTimeMillis();
		FileStatsResponse response = apiService.getDirectoryStats(path, forceCalculatable);
		log.debug("[{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), GET_DIRECTORY_STATS, response);
		return response;
	}

	/**
	 * Get stats for a paths (plural)
	 */
	private static final String GET_DIRECTORIES_STATS = "/getDirectoriesStats";

	@RequestMapping(value = GET_DIRECTORIES_STATS, method = RequestMethod.GET)
	public List<FileStatsResponse> getDirectoriesStats(
			@RequestParam(name = "path", required = false) Set<String> pathsSet,
			@RequestParam(name = "forceCalculatable", required = false, defaultValue = "false") boolean forceCalculatable,
			@RequestParam(name = "refresh", required = false, defaultValue = "false") boolean refresh)
			throws ParseException {
		long t = System.currentTimeMillis();
		List<FileStatsResponse> response = apiService.getDirectoriesStats(pathsSet, forceCalculatable, refresh);
		log.debug("[{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), GET_DIRECTORIES_STATS, response);
		return response;
	}

	/**
	 * Enables a root directory (which creates the child directories and adds a watcher
	 * service to the directories)
	 */
	private static final String POST_DOWNLOAD_PATH = "/downloadPaths";

	@RequestMapping(value = POST_DOWNLOAD_PATH)
	public int downloadPath(@RequestParam(name = "path", required = false) Set<String> pathsSet) throws ParseException {
		long t = System.currentTimeMillis();
		int c = apiService.downloadPaths(pathsSet);
		log.debug("[{}][{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), POST_DOWNLOAD_PATH, c, pathsSet);
		return c;
	}

	/**
	 * delete files
	 */
	private static final String POST_DELETE_PATH = "/deletePaths";

	@RequestMapping(value = POST_DELETE_PATH)
	public int deletePaths(@RequestParam(name = "path", required = false) Set<String> pathsSet) throws ParseException {
		long t = System.currentTimeMillis();
		int c = apiService.deletePaths(pathsSet);
		log.debug("[{}][{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), POST_DELETE_PATH, c, pathsSet);
		return c;
	}

	/**
	 * List tasks
	 */
	private static final String LIST_TASKS = "/listTasks";

	@RequestMapping(value = LIST_TASKS)
	public ListTasksResponse listTasks() throws ParseException {
		long t = System.currentTimeMillis();
		ListTasksResponse response = apiService.ListTasks();
		log.debug("[{}][{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), LIST_TASKS, response);
		return response;
	}

}
