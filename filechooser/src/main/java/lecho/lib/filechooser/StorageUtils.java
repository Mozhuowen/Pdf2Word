package lecho.lib.filechooser;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Based on http://stackoverflow.com/a/19982338/265597
 * 
 */
public class StorageUtils {

	private static final String TAG = "StorageUtils";

	public static class StorageInfo {

		public final String path;
		public final boolean readonly;
		public final boolean removable;
		public final int number;

		StorageInfo(String path, boolean readonly, boolean removable, int number) {
			this.path = path;
			this.readonly = readonly;
			this.removable = removable;
			this.number = number;
		}

		public String getDisplayName(Context context) {
			StringBuilder res = new StringBuilder();
			if (!removable) {
				res.append(context.getString(R.string.fc_internal_storage));
			} else if (number > 1) {
				res.append(context.getString(R.string.fc_external_storage) + number);
			} else {
				res.append(context.getString(R.string.fc_external_storage));
			}
			if (readonly) {
				res.append(context.getString(R.string.fc_read_only));
			}
			return res.toString();
		}
	}

	public static List<StorageInfo> getStorageList() {

		List<StorageInfo> list = new ArrayList<StorageInfo>();
		String path = Environment.getExternalStorageDirectory().getPath();
		boolean isPathRemovable = Environment.isExternalStorageRemovable();
		String pathState = Environment.getExternalStorageState();
		boolean ifPathAvailable = pathState.equals(Environment.MEDIA_MOUNTED)
				|| pathState.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
		boolean isPathReadonly = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);

		HashSet<String> paths = new HashSet<String>();
		int removableNumber = 1;

		if (ifPathAvailable) {
			paths.add(path);
			list.add(new StorageInfo(path, isPathReadonly, isPathRemovable, isPathRemovable ? removableNumber++ : -1));
		}

		BufferedReader bufReader = null;
		try {
			bufReader = new BufferedReader(new FileReader("/proc/mounts"));
			String line;
			while ((line = bufReader.readLine()) != null) {
				if (line.contains("vfat") || line.contains("/mnt")) {
					StringTokenizer tokens = new StringTokenizer(line, " ");

					tokens.nextToken(); // device

					String mountPoint = tokens.nextToken(); // mount point
					if (paths.contains(mountPoint)) {
						continue;
					}

					tokens.nextToken(); // file system

					List<String> flags = Arrays.asList(tokens.nextToken().split(",")); // flags

					boolean readonly = flags.contains("ro");

					if (line.contains("/dev/block/vold")) {
						if (!line.contains("/mnt/secure") && !line.contains("/mnt/asec") && !line.contains("/mnt/obb")
								&& !line.contains("/dev/mapper") && !line.contains("tmpfs")) {
							paths.add(mountPoint);
							list.add(new StorageInfo(mountPoint, readonly, true, removableNumber++));
						}
					}
				}
			}

		} catch (FileNotFoundException ex) {
			Log.e(TAG, "Error listing storages", ex);

		} catch (IOException ex) {
			Log.e(TAG, "Error listing storages", ex);

		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException ex) {
					Log.e(TAG, "Error listing storages", ex);
				}
			}
		}

		return list;
	}
	
	public static List<StorageInfo> getStorageListx() {
		List<String> pathlist = getSDCardPaths();
		List<StorageInfo> list = new ArrayList<StorageInfo>();
		int removableNumber = 1;
		for (int i=0;i<pathlist.size();i++ ){
			list.add(new StorageInfo(pathlist.get(i),false,false,removableNumber++));
//			Log.v("path info: ", pathlist.get(i));
		}
		
		return list;
	}
	

	public static List<String> getSDCardPaths() {
		List<String> sdcardPaths = new ArrayList<String>();
		String cmd = "cat /proc/mounts";
		Runtime run = Runtime.getRuntime();
		try {
			Process p = run.exec(cmd);
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader inBr = new BufferedReader(new InputStreamReader(in));

			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
//				LogUtil.i("CommonUtil:getSDCardPath", lineStr);

				String[] temp = TextUtils.split(lineStr, " ");
				String result = temp[1];
				File file = new File(result);
				if (file.isDirectory() && file.canRead() && file.canWrite()) {
//					LogUtil.d("directory can read can write:",
//							file.getAbsolutePath());
					sdcardPaths.add(result);

				}

				if (p.waitFor() != 0 && p.exitValue() == 1) {
				}
			}
			inBr.close();
			in.close();
		} catch (Exception e) {
//			LogUtil.e("CommonUtil:getSDCardPath", e.toString());

			sdcardPaths.add(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
		}

		optimize(sdcardPaths);
		for (Iterator iterator = sdcardPaths.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
		}
		return sdcardPaths;
	}

	private static void optimize(List<String> sdcaredPaths) {
                if (sdcaredPaths.size() == 0) {
                    return;
                 }
		int index = 0;
		while (true) {
			if (index >= sdcaredPaths.size() - 1) {
				String lastItem = sdcaredPaths.get(sdcaredPaths.size() - 1);
				for (int i = sdcaredPaths.size() - 2; i >= 0; i--) {
					if (sdcaredPaths.get(i).contains(lastItem)) {
						sdcaredPaths.remove(i);
					}
				}
				return;
			}

			String containsItem = sdcaredPaths.get(index);
			for (int i = index + 1; i < sdcaredPaths.size(); i++) {
				if (sdcaredPaths.get(i).contains(containsItem)) {
					sdcaredPaths.remove(i);
					i--;
				}
			}

			index++;
		}

	}
}
