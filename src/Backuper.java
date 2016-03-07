import java.io.File;
import java.io.IOException;

public abstract class Backuper {
	abstract void backupFile(File file) throws IOException;
	abstract void backupFile(String filename) throws IOException;
	abstract void restoreFile(String filename) throws IOException;
	
}
