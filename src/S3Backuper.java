import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;

import javax.swing.JOptionPane;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.model.Paths;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;


public class S3Backuper extends Backuper {
	private static AWSCredentials credentials = null;
	private static TransferManager tx;
	private static String bucketName;
	private Upload upload;


	S3Backuper() {
		/*
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * (C:\\Users\\nir\\.aws\\credentials).
		 *
		 * TransferManager manages a pool of threads, so we create a
		 * single instance and share it throughout our application.
		 */
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (C:\\Users\\nir\\.aws\\credentials), and is in valid format.",
							e);
		}

		AmazonS3 s3 = new AmazonS3Client(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(usWest2);
		tx = new TransferManager(s3);

		bucketName = "s3-upload-sdk-sample-" + credentials.getAWSAccessKeyId().toLowerCase();
		createAmazonS3Bucket();

	}
	@Override

	void backupFile(File file) throws IOException {
		
		//ProgressListener progressListener = new ProgressListener() {
		//public void progressChanged(ProgressEvent progressEvent) {
		//if (upload == null) return;
		
		//				pb.setValue((int)upload.getProgress().getPercentTransferred());
		
		//			switch (progressEvent.getEventCode()) {
		//		case ProgressEvent.COMPLETED_EVENT_CODE:
		//		pb.setValue(100);
		// break;
		//	case ProgressEvent.FAILED_EVENT_CODE:
		File fileToUpload = file;
		System.out.println("backupFile: " + file.getAbsolutePath());
		try {
			
			GetObjectMetadataRequest md5request = new GetObjectMetadataRequest(bucketName, fileToUpload.getCanonicalPath());
			
			ObjectMetadata md5check = tx.getAmazonS3Client().getObjectMetadata(md5request);
			//MessageDigest md = MessageDigest.getInstance("MD5");
			
			// fuck this shit, I want a smoke.
			// InputStream is = Files.newInputStream(Paths.get(fileToUpload.getCanonicalPath()));
			
			
			System.out.println("on file " + fileToUpload.getCanonicalPath() + " we have listed an MD5sum of "+ md5check.getETag() + " and " + md5check.getContentLength());
			PutObjectRequest request = new PutObjectRequest(
					bucketName, fileToUpload.getCanonicalPath(), fileToUpload);
			//.withGeneralProgressListener(progressListener);
			upload = tx.upload(request);
			
			AmazonClientException e = upload.waitForException();
			System.out.println("Unable to upload file to Amazon S3: " + e.getMessage());
			
		} 
		catch (InterruptedException e) { System.out.println("Caught Intterupted e" + e.getMessage());}
		catch (NullPointerException e) { System.out.println("that was me, you bastards "+ e.getMessage()); }
	}


	@Override
	void backupFile(String filename) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	void restoreFile(String filename) throws IOException {
		// TODO Auto-generated method stub

	}

	private void createAmazonS3Bucket() {
		try {
			if (tx.getAmazonS3Client().doesBucketExist(bucketName) == false) {
				tx.getAmazonS3Client().createBucket(bucketName);
			}
		} catch (AmazonClientException ace) {
			System.out.println("Unable to create a new Amazon S3 bucket: " + ace.getMessage());
		}
	}

}
