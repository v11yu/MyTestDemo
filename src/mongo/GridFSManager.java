package mongo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * 这是mongodb的GridFS的管理类，单例模式
 * @author Kontrol
 *
 */
public class GridFSManager {
	
	private static GridFS gridFS = null;
	
	/**
	 * 私有构造函数
	 */
	private GridFSManager(){
		
	}
	
	/**
	 * 初始化
	 */
	private static synchronized void init(){
		if (gridFS == null) {
			DB db = MongoDBManager.getUniqueMongoDBManager().getDB();
			gridFS = new GridFS(db);
		}
	}
	
	/**
	 * 保存文件
	 * @param gridFS
	 * @param file
	 */
	public static void saveFile(File file){
		init();
		String name = file.getName();
		//先将文件给删除
		gridFS.remove(new BasicDBObject("filename", name));
		try {
			FileInputStream fis = new FileInputStream(file);
			GridFSInputFile gridFSInputFile = gridFS.createFile(fis);
			gridFSInputFile.setFilename(name);	//这个必须的！要不然怎样获取
			gridFSInputFile.save();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 删除已有文件
	 * @param file
	 */
	public static void removeFile(File file){
		init();
		String name = file.getName();
		gridFS.remove(new BasicDBObject("filename", name));
	}
	/** 
     * 根据文件名返回文件
     * @param id 
     * @return 
     */  
    public static GridFSDBFile getByFileName(String fileName){
    	init();
        DBObject query  = new BasicDBObject("filename", fileName);  
        GridFSDBFile gridFSDBFile = gridFS.findOne(query);  
        return gridFSDBFile;  
    }
    
    public static boolean exist(String fileName){
    	GridFSDBFile gridFSDBFile = getByFileName(fileName);
    	if (gridFSDBFile!=null) {
			return true;
		} else {
			return false;
		}
    }
    
    /**
     * 将数据库中的fileName的文件导出至filePath的磁盘中
     * @param fileName
     * @param filePath
     * @return
     */
    public static File getByFileName(String fileName, String filePath){
    	try {
	    	File file = new File(filePath);
	    	if (file.exists()) {
				file.createNewFile();
			}
	    	GridFSDBFile gridFSDBFile = getByFileName(fileName);
	    	if (gridFSDBFile != null) {
				try {
					gridFSDBFile.writeTo(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    	return file;
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
	public static void main(String[] args) {
//		File file = new File("gexf");
//		File[] gexfs = file.listFiles();
//		for (int i = 0; i < gexfs.length && i<10; i++) {
//			final File gexfFile = gexfs[i];
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					saveFile(gexfFile);
//				}
//			}).start();
//		}
//		getByFileName("3632514919061036.gexf", "E:\\Android\\3632514919061036.gexf");
		System.out.print(exist("aa"));
	}
    
}
