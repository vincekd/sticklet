package com.sticklet.www.exclude;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.config.ConfigurableComponent;
import net.sourceforge.stripes.controller.FileUploadLimitExceededException;
import net.sourceforge.stripes.controller.multipart.MultipartWrapperFactory;

public class EmptyMultipartWrapper implements ConfigurableComponent, MultipartWrapperFactory {
	public net.sourceforge.stripes.controller.multipart.MultipartWrapper wrap(HttpServletRequest request) throws IOException, FileUploadLimitExceededException {
		return null;
	}
	
	@Override
	public void init(net.sourceforge.stripes.config.Configuration arg0) throws Exception {

	}
}