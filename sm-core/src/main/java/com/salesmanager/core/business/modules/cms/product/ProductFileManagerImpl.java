package com.salesmanager.core.business.modules.cms.product;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesmanager.core.business.constants.Constants;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.utils.CoreConfiguration;
import com.salesmanager.core.business.utils.ProductImageCropUtils;
import com.salesmanager.core.business.utils.ProductImageSizeUtils;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.file.ProductImageSize;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.content.FileContentType;
import com.salesmanager.core.model.content.ImageContentFile;
import com.salesmanager.core.model.content.OutputContentFile;

public class ProductFileManagerImpl extends ProductFileManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductFileManagerImpl.class);

	private ProductImagePut uploadImage;
	private ProductImageGet getImage;
	private ProductImageRemove removeImage;

	private CoreConfiguration configuration;

	private final static String PRODUCT_IMAGE_HEIGHT_SIZE = "PRODUCT_IMAGE_HEIGHT_SIZE";
	private final static String PRODUCT_IMAGE_WIDTH_SIZE = "PRODUCT_IMAGE_WIDTH_SIZE";
	private final static String CROP_UPLOADED_IMAGES = "CROP_UPLOADED_IMAGES";

	public CoreConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(CoreConfiguration configuration) {
		this.configuration = configuration;
	}

	public ProductImageRemove getRemoveImage() {
		return removeImage;
	}

	public void setRemoveImage(ProductImageRemove removeImage) {
		this.removeImage = removeImage;
	}

	public void addProductImage(ProductImage productImage, ImageContentFile contentImage) throws ServiceException {
		String sMethod = "addProductImage";
		loggerDebugM(sMethod, "start");
		try {

			String extension = getCalculatedExtension(contentImage);
			String fileName = productImage.getProduct().getId() + "tmpLarge";

			BufferedImage initializedBuffer = getBufferFromInputData(productImage, contentImage);

			String newFileName = fileName + "_large";
			File largeFileCreated = createFileFromBufferedImage(newFileName, extension, initializedBuffer);
			if (largeFileCreated == null) {
				loggerDebug("return-image-created-from-temp-not-exists");
				return;
			}
						
			boolean createdFromFileLG = executeAddProductImageFromFile(largeFileCreated, productImage,FileContentType.PRODUCTLG);
			if (createdFromFileLG) {
				loggerDebug("return-image-created-from-temp");
			}
			
			boolean createdFromTemp = executeAddProductImageFromFile(largeFileCreated, productImage,FileContentType.PRODUCT);
			if (createdFromTemp) {
				loggerDebug("return-image-created-from-temp");
			}

			ProductImageImportData dt = getImageData();
			if( dt == null) {
				return;
			}
			
			BufferedImage providedFileBuffer = getBuferFromFile(newFileName,extension);
			if( providedFileBuffer == null) {
				loggerDebug("providedFileBuffer-NULL");
				return;
			}
			
			BufferedImage croppedImage = getCalculatedCroppedImage(providedFileBuffer, dt.getWidth(),
					dt.getHeight());
			if( croppedImage == null) {
				loggerDebug("croppedImage-NULL");
				return;
			}
			
			String croppedFileName = fileName + "_cropped";
			File croppedFileCreated = createFileFromBufferedImage(croppedFileName, extension, croppedImage);
			if (croppedFileCreated == null) {
				loggerDebug("croppedFileCreated-NO");
			}
			
			BufferedImage largeResizedImage = getCalculatedResizedImage(croppedImage, dt.getWidth(),
					dt.getHeight());			
			String largeResizedFileName = fileName + "_large_resized";
			File largeResizedFileCreated = createFileFromBufferedImage(largeResizedFileName, extension, largeResizedImage);
			if (largeResizedFileCreated == null) {
				loggerDebug("largeResizedFileCreated-NO");
			}
			
			boolean createdFromPersisted = executeAddProductImageFromFile( largeResizedFileCreated,
					productImage,FileContentType.PRODUCT);
			if (!createdFromPersisted) {
				loggerDebug("createdFromPersisted-NO");
			}

		} catch (Exception e) {
			LOGGER.error("EXCEPTION:" + e.getMessage());
			loggerDebugM(sMethod, "return-exception");
		} finally {
			closeInputStream(productImage);
			loggerDebugM(sMethod, "return");
		}
	}
	
	private ProductImageImportData getImageData() {
		String sMethod = "addProductImage";
		loggerDebugM(sMethod, "start");
		
		ProductImageImportData dt = new ProductImageImportData();
		String slargeImageHeight = configuration.getProperty(PRODUCT_IMAGE_HEIGHT_SIZE);
		String slargeImageWidth = configuration.getProperty(PRODUCT_IMAGE_WIDTH_SIZE);
		boolean sizeConfigured = !StringUtils.isBlank(slargeImageHeight) && !StringUtils.isBlank(slargeImageWidth);

		if (!sizeConfigured) {
			return null;
		}

		int largeImageHeight = Integer.parseInt(slargeImageHeight);
		int largeImageWidth = Integer.parseInt(slargeImageWidth);
		boolean imageSizeStatus = checkImageSize(largeImageWidth, largeImageHeight);

		if (!imageSizeStatus) {
			loggerDebug("return-image-not-resized-wrong-size");
			loggerDebugM(sMethod, "return");
			return null;
		}
		dt.setWidth(largeImageWidth);
		dt.setHeight(largeImageHeight);
		return dt;
	}
	
	private boolean closeInputStream(ProductImage productImage) {
		try {
			productImage.getImage().close();
			return true;
		} catch (Exception ignore) {
			LOGGER.error("EXCEPTION:" + ignore.getMessage());
			return false;
		}
	}
	private BufferedImage getBufferFromInputData(ProductImage productImage, ImageContentFile contentImage) {
		String sMethod = "getBufferFromInputData";
		loggerDebugM(sMethod, "start");

		try {
			ByteArrayOutputStream baos2 = getCalculatedBufferedImage(contentImage);

			if (baos2 == null) {
				loggerDebugM(sMethod, "return");
				return null;
			}

			InputStream is2 = new ByteArrayInputStream(baos2.toByteArray());
			BufferedImage bufferedImage = ImageIO.read(is2);

			if (bufferedImage == null) {
				LOGGER.error("Cannot read image format for " + productImage.getProductImage());
				loggerDebugM(sMethod, "return");
				return null;
			}

			loggerDebugM(sMethod, "end");
			
			return bufferedImage;
		} catch (Exception e) {
			LOGGER.error("EXCEPTION-ADD-PRODUCT-IMAGE:" + e.getMessage());
			loggerDebugM(sMethod, "return");
			return null;
		}

	}

	private BufferedImage createInitializedData(ProductImage productImage, ImageContentFile contentImage,FileContentType fileContentType) {
		String sMethod = "createInitializedData";
		loggerDebugM(sMethod, "start");

		try {
			ByteArrayOutputStream baos2 = getCalculatedBufferedImage(contentImage);

			if (baos2 == null) {
				loggerDebugM(sMethod, "return");
				return null;
			}

			InputStream is2 = new ByteArrayInputStream(baos2.toByteArray());
			BufferedImage bufferedImage = ImageIO.read(is2);

			if (bufferedImage == null) {
				LOGGER.error("Cannot read image format for " + productImage.getProductImage());
				loggerDebugM(sMethod, "return");
				return null;
			}

			boolean initData = setInitializedData(baos2, productImage, contentImage,fileContentType);
			if (!initData) {
				loggerDebugM(sMethod, "return");
				return null;
			}
			loggerDebugM(sMethod, "end");
			
			return bufferedImage;
		} catch (Exception e) {
			LOGGER.error("EXCEPTION-ADD-PRODUCT-IMAGE:" + e.getMessage());
			loggerDebugM(sMethod, "return");
			return null;
		}

	}

	private boolean setInitializedData(ByteArrayOutputStream baos, ProductImage productImage,
			ImageContentFile contentImage,FileContentType fileContentType) {
		try {
			InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
			contentImage.setFile(is1);
			contentImage.setFileContentType(fileContentType);
			uploadImage.addProductImage(productImage, contentImage);

		} catch (Exception e) {
			LOGGER.error("EXCEPTION-ADD-PRODUCT-IMAGE:" + e.getMessage());
			return false;
		}
		return true;
	}
	
	private File createFileFromBufferedImage(String fileName, String extension, BufferedImage largeResizedImage) {
		String sMethod = "createFileFromBufferedImage";
		loggerDebugM(sMethod, "start");
		
		String createdFile = "files/images/" + fileName + "." + extension;
		File fileLarge = new File(createdFile);
		String absPath = fileLarge.toPath().toAbsolutePath().toString();

		try {
			ImageIO.write(largeResizedImage, extension, fileLarge);
		} catch (Exception ex) {
			loggerDebug("Error:" + ex.getMessage());
		}

		loggerDebug("created-image-fileLarge-absolute-path:" + absPath);
		loggerDebug("created-image-fileLarge-abs-path-file :" + fileLarge.getAbsolutePath());
		loggerDebug("created-image-fileLarge-exists:" + fileLarge.exists());
		if (!fileLarge.exists()) {
			return null;
		}

		try {
			BufferedImage bImage = ImageIO.read(fileLarge);
			loggerDebug("created-image-fileLarge-getHeight :" + bImage.getHeight());
			loggerDebug("created-image-fileLarge-getWidth :" + bImage.getWidth());
		} catch (Exception ex) {
			loggerDebug("Error:" + ex.getMessage());
		}
		loggerDebugM(sMethod, "end");
		return fileLarge;
	}

	private BufferedImage getBuferFromFile(String fileName, String extension) {
		String sMethod = "getBuferFromFile";
		loggerDebugM(sMethod, "start");		
		try {
			String createdFile = "files/images/" + fileName + "." + extension;
			File fileLarge = new File(createdFile);			
			BufferedImage bImage = ImageIO.read(fileLarge);
			loggerDebug("created-image-fileLarge-getHeight :" + bImage.getHeight());
			loggerDebug("created-image-fileLarge-getWidth :" + bImage.getWidth());
			loggerDebugM(sMethod, "end");
			return bImage;
		} catch (Exception ex) {
			loggerDebug("Error:" + ex.getMessage());
			return null;
		}		
	}
	
	private boolean createFileAndSetData(String fileName, String extension, BufferedImage imageBuffer,
			ProductImage productImage,FileContentType fileContentType) {
		try {
			File imgFile = createFileFromBufferedImage(fileName,extension,imageBuffer);
			executeAddProductImageFromFile(imgFile, productImage,fileContentType);			
		} catch (Exception e) {
			LOGGER.error("EXCEPTION-ADD-PRODUCT-IMAGE:" + e.getMessage());
			return false;
		}
		return true;
	}	

	private boolean setFromCalulatedTempFile(String fileName, String extension, BufferedImage largeResizedImage,
			ProductImage productImage,FileContentType fileContentType) {
		try {
			File tempLarge = File.createTempFile(fileName, "." + extension);
			ImageIO.write(largeResizedImage, extension, tempLarge);
			executeAddProductImageFromFile(tempLarge, productImage,fileContentType);
			tempLarge.delete();
		} catch (Exception e) {
			LOGGER.error("EXCEPTION-ADD-PRODUCT-IMAGE:" + e.getMessage());
			return false;
		}
		return true;
	}	

	private boolean executeAddProductImageFromFile(File file, ProductImage productImage,FileContentType fileContentType) {
		try {
			try (FileInputStream isLarge = new FileInputStream(file)) {
				ImageContentFile largeContentImage = new ImageContentFile();
				largeContentImage.setFileContentType(fileContentType);
				largeContentImage.setFileName(productImage.getProductImage());
				largeContentImage.setFile(isLarge);
				uploadImage.addProductImage(productImage, largeContentImage);
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("EXCEPTION-ADD-PRODUCT-IMAGE:" + e.getMessage());
		}
		return false;
	}

	private boolean checkImageSize(int largeImageWidth, int largeImageHeight) {
		if (largeImageHeight <= 0 || largeImageWidth <= 0) {
			String sizeMsg = "Image configuration set to an invalid value [PRODUCT_IMAGE_HEIGHT_SIZE] "
					+ largeImageHeight + " , [PRODUCT_IMAGE_WIDTH_SIZE] " + largeImageWidth;
			LOGGER.error(sizeMsg);
			return false;
		}
		return true;
	}

	private BufferedImage getCalculatedResizedImage(BufferedImage currentImage, int largeImageWidth,
			int largeImageHeight) {
		BufferedImage largeResizedImage = currentImage;
		try {
			if (currentImage.getWidth() > largeImageWidth || largeResizedImage.getHeight() > largeImageHeight) {
				largeResizedImage = ProductImageSizeUtils.resizeWithRatio(currentImage, largeImageWidth,
						largeImageHeight);
			} else {
				largeResizedImage = currentImage;
			}
			return largeResizedImage;
		} catch (Exception ex) {
			loggerDebug("Error:" + ex.getMessage());
			return currentImage;
		}
	}

	private BufferedImage getCalculatedCroppedImage(BufferedImage currentImage, int largeImageWidth,
			int largeImageHeight) {
		BufferedImage bufferedImage = currentImage;
		try {

			if (!StringUtils.isBlank(configuration.getProperty(CROP_UPLOADED_IMAGES))
					&& configuration.getProperty(CROP_UPLOADED_IMAGES).equals(Constants.TRUE)) {
				ProductImageCropUtils utils = new ProductImageCropUtils(bufferedImage, largeImageWidth,
						largeImageHeight);
				if (utils.isCropeable()) {
					bufferedImage = utils.getCroppedImage();
				}
			}
			return bufferedImage;
		} catch (Exception ex) {
			loggerDebug("Error:" + ex.getMessage());
			return currentImage;
		}
	}

	private ByteArrayOutputStream getCalculatedBufferedImage(ImageContentFile contentImage) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = contentImage.getFile().read(buffer)) > -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();
			return baos;
		} catch (Exception ex) {
			loggerDebug("Error:" + ex.getMessage());
			return null;
		}

	}


	private String getCalculatedExtension(ImageContentFile contentImage) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();

		String contentType = fileNameMap.getContentTypeFor(contentImage.getFileName());
		String extension = null;
		if (contentType != null) {
			extension = contentType.substring(contentType.indexOf('/') + 1, contentType.length());
		}

		if (extension == null) {
			// extension = "jpeg";
			extension = "jpg";
		}
		extension = "png";
		return extension;

	}

	public void imageTestWrite() {
		BufferedImage bImage = null;
		try {
			String fullPath = "C:\\lkd\\ht\\apps_shopizer_l\\src\\app\\shopizer\\sm-shop\\";
			String initialImageName = fullPath + "files/images/img__ini.png";
			String createdImageName = fullPath + "files/images/img__ini_created.jpg";
			File initialImage = new File(initialImageName);
			loggerDebug("test-image-initial-image-exists :" + initialImage.exists());

			bImage = ImageIO.read(initialImage);
			loggerDebug("test-image-bImage-getHeight :" + bImage.getHeight());
			loggerDebug("test-image-bImage-getWidth :" + bImage.getWidth());
			File createdFile = new File(createdImageName);
			ImageIO.write(bImage, "jpg", createdFile);
			loggerDebug("test-image-created-path :" + createdFile.getAbsolutePath());
			loggerDebug("test-image-created-exists :" + createdFile.exists());

			String fullCreatedPath2 = "C:\\lkd\\";
			String createdImageName2 = fullCreatedPath2 + "files\\images\\img__ini_created.png";
			File createdFile2 = new File(createdImageName2);
			ImageIO.write(bImage, "png", createdFile2);
			loggerDebug("test-image-created2-path :" + createdFile2.getAbsolutePath());
			loggerDebug("test-image-created2-exists :" + createdFile2.exists());

		} catch (Exception e) {
			loggerDebug("Exception occured :" + e.getMessage());
		}

		loggerDebug("Images were written succesfully.");
	}

	private String getDbgClassName() {
		return "ProductFileManagerImpl::";
	}

	private void loggerDebug(String ttx) {
		String stx = getDbgClassName() + ttx;
		LOGGER.debug(stx);
	}

	private void loggerDebugM(String sMethod, String ttx) {
		String stx = getDbgClassName() + ":" + sMethod + ":" + ttx;
		LOGGER.debug(stx);
	}

	private void loggerExceptionM(String sMethod, String ttx, Exception ex) {
		String stx = getDbgClassName() + ":" + sMethod + ":" + ttx;

		LOGGER.debug(stx + "exc-start");
		LOGGER.error(ex.getMessage());
		LOGGER.debug(stx + "exc-end");
	}

	public OutputContentFile getProductImage(ProductImage productImage) throws ServiceException {
		// will return original
		return getImage.getProductImage(productImage);
	}

	@Override
	public List<OutputContentFile> getImages(final String merchantStoreCode, FileContentType imageContentType)
			throws ServiceException {
		// will return original
		return getImage.getImages(merchantStoreCode, FileContentType.PRODUCT);
	}

	@Override
	public List<OutputContentFile> getImages(Product product) throws ServiceException {
		return getImage.getImages(product);
	}

	@Override
	public void removeProductImage(ProductImage productImage) throws ServiceException {

		this.removeImage.removeProductImage(productImage);

		/*
		 * ProductImage large = new ProductImage();
		 * large.setProduct(productImage.getProduct()); large.setProductImage("L" +
		 * productImage.getProductImage());
		 *
		 * this.removeImage.removeProductImage(large);
		 *
		 * ProductImage small = new ProductImage();
		 * small.setProduct(productImage.getProduct()); small.setProductImage("S" +
		 * productImage.getProductImage());
		 *
		 * this.removeImage.removeProductImage(small);
		 */

	}

	@Override
	public void removeProductImages(Product product) throws ServiceException {

		this.removeImage.removeProductImages(product);

	}

	@Override
	public void removeImages(final String merchantStoreCode) throws ServiceException {

		this.removeImage.removeImages(merchantStoreCode);

	}

	public ProductImagePut getUploadImage() {
		return uploadImage;
	}

	public void setUploadImage(ProductImagePut uploadImage) {
		this.uploadImage = uploadImage;
	}

	public ProductImageGet getGetImage() {
		return getImage;
	}

	public void setGetImage(ProductImageGet getImage) {
		this.getImage = getImage;
	}

	@Override
	public OutputContentFile getProductImage(String merchantStoreCode, String productCode, String imageName)
			throws ServiceException {
		return getImage.getProductImage(merchantStoreCode, productCode, imageName);
	}

	@Override
	public OutputContentFile getProductImage(String merchantStoreCode, String productCode, String imageName,
			ProductImageSize size) throws ServiceException {
		return getImage.getProductImage(merchantStoreCode, productCode, imageName, size);
	}

}
