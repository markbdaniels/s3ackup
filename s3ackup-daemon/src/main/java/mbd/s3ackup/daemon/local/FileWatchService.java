package mbd.s3ackup.daemon.local;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileWatchService implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(FileWatchService.class);

	private final WatchService watchService;
	private Executor executor = Executors.newSingleThreadExecutor();
	private Consumer<Path> saveFunction;

	public FileWatchService(Path rootDir, Consumer<Path> saveFunction) throws IOException {
		this.saveFunction = saveFunction;
		watchService = FileSystems.getDefault().newWatchService();
		Files.walkFileTree(rootDir, new RegisterDirectoriesVisitor());
		executor.execute(this);
	}

	public void destroy() {
		try {
			watchService.close();
		}
		catch (IOException e) {
			log.error("unabled to stop file watcher service", e);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				WatchKey key;
				while ((key = watchService.take()) != null) {
					if (!key.isValid()) {
						key.cancel();
						continue;
					}

					List<WatchEvent<?>> pollEvents = key.pollEvents();
					for (WatchEvent<?> event : pollEvents) {
						final Path file = (Path) event.context();
						Path dir = (Path) key.watchable();
						Path fullPath = dir.resolve(file);

						log.debug("localPath[{}]", fullPath);
						switch (event.kind().name()) {
						case "ENTRY_CREATE":
						case "ENTRY_MODIFY":
							saveAllChildren(fullPath);
							break;
						case "ENTRY_DELETE":
							// not deleting - too dangerous - might just wipe
							// out all files in the bucket
							// deleteFunction.accept(fullPath);
							break;
						default:
							log.warn("unhandled event kind[{}]", event.kind());
							break;
						}
					}
					key.reset();
				}
			}
			catch (ClosedWatchServiceException e) {
				// file watcher is closed
				return;
			}
			catch (Exception e) {
				log.error("Exception while polling for file changes", e);
			}
		}
	}

	/*
	 * If it is a full directory copied over - there may be child files which also need to
	 * be part of the event
	 */
	private void saveAllChildren(Path fullPath) throws IOException {
		if (Files.isDirectory(fullPath)) {
			Files.walkFileTree(fullPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					saveFunction.accept(file);
					return FileVisitResult.CONTINUE;
				}
			});
		}
		saveFunction.accept(fullPath);
	}

	public class RegisterDirectoriesVisitor extends SimpleFileVisitor<Path> {
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
			try {
				log.debug("watching [{}]", dir);
				dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
			}
			catch (IOException e) {
				log.error("unable to watch dir", e);
			}
			return CONTINUE;
		}
	}
}
