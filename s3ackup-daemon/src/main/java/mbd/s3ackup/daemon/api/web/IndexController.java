package mbd.s3ackup.daemon.api.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

	@Value(value = "${server.port:56000}")
	private int port;

	@RequestMapping(value = { "{path:(?!static).*$}/**" })
	public ModelAndView index() {
		ModelAndView mv = new ModelAndView("index");
		mv.addObject("baseApiUrl", String.format("http://localhost:%s", port));
		return mv;
	}

}
