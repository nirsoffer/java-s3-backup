import java.io.File;
import java.util.NoSuchElementException;
import java.io.File;
import java.util.*;

public class TreeBackup {
	private String _root;



	TreeBackup(String root) {
		_root = root;
	}

	void traverse(String rootstring,Backuper backuper){ 

		File f=null;
		try { f=new java.io.File(rootstring); } catch (Exception e) { System.err.println(e.toString()); } 
		Deque<File> stack = new ArrayDeque<File>();
		System.err.println("I am in traverse "+ rootstring);
		stack.push(f);
		try  { 
			while ((f=stack.pop()) != null) {
				System.out.println("in loop "+ f.getAbsolutePath() + " " + f.listFiles());

				File[] list=f.listFiles();
				if (list==null) {
					System.out.println("list is null, how odd");
					System.out.println("f is " + f.getCanonicalPath());
				} else {
					for (int i=0;i<list.length;i++) {
						if (list[i].isDirectory()) {
							stack.push(list[i]);
						} else {
							backuper.backupFile(list[i]);
						}
					}
				}

			}

		}
		catch (NoSuchElementException e) {
			System.out.println("Hurray we are done! " + e.getMessage() );
		}
		catch (Exception e) {
			System.out.println("Well, shit. Exception and stuff. " +  e.getMessage() + " " + e.toString());
		}

	}

	void traverse(Backuper backuper) {
		traverse(_root,backuper);
	}
}
