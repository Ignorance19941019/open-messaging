package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author XF
 * ����д������ڶ��߳�����£�����һ���ļ��컹�Ƕ���ļ���
 */
public class TestMultiFilesPerformance {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		for(int i=0; i<1000; i++){
			writeStr.add("TestMultiFilesPerformance test string "+i);
		}
		TestMultiFilesPerformance tmfp = new TestMultiFilesPerformance();
		Runnable wm = tmfp.writeMulti;
		Runnable ws = tmfp.writeSingle;
		startTime = System.currentTimeMillis();
		
		//����һ���ļ��컹�Ƕ���ļ�
		for(int i=0; i<20; i++){
			//ÿ���߳�дһ���ļ�����180ms����
			Thread tm = new Thread(wm);
			tm.start();
			//�����߳�дһ���ļ�����230ms����
			Thread ts = new Thread(ws);
			ts.start();
		}
		Thread.sleep(1000);
		System.out.println("ÿ���̷ֿ߳�д����̺߳�ʱ(ms)�� "+mTime);
		System.out.println("�����߳�дͬһ���ļ���ʱ(ms)�� "+sTime);

	}
	
	private static long startTime;
	private static long mTime;
	private static long sTime;
	private static List<String> writeStr = new ArrayList<String>(1000);
	
	/**
	 * ÿ���߳�дһ���ļ���
	 */
	private Runnable writeMulti = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String fileName = Thread.currentThread().getName();
			File file = new File("test/"+fileName+".txt");
			File test = new File("test");
			if(!test.isDirectory()){
				test.mkdir();
			}
			if(!file.isFile()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
				os.writeObject(writeStr);
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				long endTime = System.currentTimeMillis();
				if(endTime - startTime> mTime){
					mTime = endTime - startTime;
				}
			}
		}
	};
	/**
	 * �����߳�дͬһ���ļ���<p>
	 * һ�������ص������Ƕ���߳�дһ���ļ����������Ļ�д��˳����ȫ���ɿء�
	 */
	private Runnable writeSingle = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			File file = new File("test/writeSingle.txt");
			if(!file.isFile()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file,true));
				os.writeObject(writeStr);
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				long endTime = System.currentTimeMillis();
				sTime = endTime - startTime;
				if(endTime - startTime> sTime){
					sTime = endTime - startTime;
				}
			}
		}
	};

}
