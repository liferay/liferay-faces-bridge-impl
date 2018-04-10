package com.liferay.faces.issue.primefaces.faces3250;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;

/**
 * 
 * @author Yeray Rodriguez (yerayrodriguez@gmail.com)
 */
@ManagedBean
@SessionScoped
public class AdvancedMode implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(AdvancedMode.class);

	private UploadedFile file;
	private String text;

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFileName() {
		return file != null ? file.getFileName() : "empty";
	}

	public void submit() {
		logger.info("Text: " + getText());
		logger.info("Uploaded file: " + getFileName());
	}

	public void handleFileUpload(FileUploadEvent event) {
		setFile(event.getFile());
		logger.info("handleFileUpload:" + getFileName());
	}

}
