package mbd.s3ackup.daemon.api.rest;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mbd.s3ackup.daemon.api.rest.dto.LoginRequest;
import mbd.s3ackup.daemon.api.rest.dto.LoginResponse;
import mbd.s3ackup.daemon.api.rest.dto.LoginTypeResponse;
import mbd.s3ackup.daemon.util.DateUtil;
import mbd.s3ackup.daemon.util.Encryptor.EncryptorException;

@RestController
@RequestMapping(value = "/api/login")
public class LoginController {

	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private LoginService loginService;

	/**
	 * Login
	 */
	private static final String SUBMIT_LOGIN = "/submit";

	@RequestMapping(value = SUBMIT_LOGIN)
	public LoginResponse submitLogin(@RequestBody LoginRequest request) throws ParseException, EncryptorException {
		long t = System.currentTimeMillis();
		LoginResponse response = loginService.login(request);
		log.info("[{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), SUBMIT_LOGIN, response);
		return response;
	}

	/**
	 * Logout
	 */
	private static final String LOGOUT = "/logout";

	@RequestMapping(value = LOGOUT)
	public void logout() throws ParseException, EncryptorException {
		long t = System.currentTimeMillis();
		loginService.logout();
		log.info("[{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), LOGOUT);
	}

	/**
	 * Determine whether user has saved login credentials
	 */
	private static final String GET_TYPE = "/type";

	@RequestMapping(value = GET_TYPE)
	public LoginTypeResponse getLoginType() throws ParseException, EncryptorException {
		long t = System.currentTimeMillis();
		LoginTypeResponse response = loginService.loginType();
		log.info("[{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), GET_TYPE, response);
		return response;
	}

	/**
	 * clear the credentials saved locally
	 */
	private static final String CLEAR_SAVED_CREDENTIALS = "/clear";

	@RequestMapping(value = CLEAR_SAVED_CREDENTIALS)
	public LoginTypeResponse clear() throws ParseException, EncryptorException {
		long t = System.currentTimeMillis();
		LoginTypeResponse response = loginService.clear();
		log.info("[{}][{}][{}]", DateUtil.getTimeDifferenceAsString(t), CLEAR_SAVED_CREDENTIALS, response);
		return response;
	}

}
