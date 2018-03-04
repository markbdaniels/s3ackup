import React, {Component} from "react";
import {connect} from "react-redux";
import LoadingBar from 'react-redux-loading-bar'
import {
  Alert,
  UncontrolledTooltip,
  Button,
  Form,
  FormGroup,
  Label,
  Input,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter
} from 'reactstrap';
import {login, loginType, clearLogin} from "../../../redux/actions";

class Login extends Component {

  constructor(props) {
    super(props);
    this.state = {
      rememberMe: false,
      accessInputValid: false,
      accessInputValue: "",
      secretInputValid: false,
      secretInputValue: "",
      pinInputValid: false,
      pinInputValue: "",
      loginFailedAlertOpen: false,
      loginFailedAlertText: ""
    };
  }

  componentDidMount() {
    if (this.props.login.success) {
      this.props.history.push("/");
    } else {
      this.props.loginType();
    }
  }

  toggleRememberMe = () => {
    this.setState({
      rememberMe: !this.state.rememberMe
    });
  }

  accessOnChange = (event) => {
    let value = event.target.value;
    let valid = /^([A-Z0-9]{20})$/.test(value);
    this.setState({accessInputValue: event.target.value, accessInputValid: valid});
  }

  secretOnChange = (event) => {
    let value = event.target.value;
    let valid = /(^|[^A-Za-z0-9/+=])[A-Za-z0-9/+=]{40}(?![A-Za-z0-9/+=])$/.test(value);
    console.log('value:', value, 'valid:', valid);
    this.setState({secretInputValue: event.target.value, secretInputValid: valid});
  }

  pinOnChange = (event) => {
    let value = event.target.value;
    let valid = /^([A-Z0-9]{4,})$/.test(value);
    this.setState({pinInputValue: event.target.value, pinInputValid: valid});
  }

  isAllFieldsValid = () => {
    return this.state.accessInputValid && this.state.secretInputValid && ((this.state.rememberMe && this.state.pinInputValid) || (!this.state.rememberMe))
  }

  toggleLoginFailedAlert = () => {
    this.setState({
      loginFailedAlertOpen: !this.state.loginFailedAlertOpen
    });
  }

  buttonLogin = () => {
    let params = {
      accessKey: this.state.accessInputValue,
      secretKey: this.state.secretInputValue,
      rememberMe: this.state.rememberMe,
      pin: this.state.pinInputValue
    };
    this.props.login(params, (data) => {
      if (data.data.success) {
        this.props.history.push("/");
        return data;
      } else {}
      return data
    });
  }

  clearLocalCredential = () => {
    this.props.clearLogin();
  }

  render() {

    let loginCredentialsSavedLocally = this.props.loginTypeResult.loginCredentialsSavedLocally;

    let tmpAccessKey = loginCredentialsSavedLocally
      ? this.props.loginTypeResult.accessKey
      : this.state.accessInputValue;
    let tmpSecretKey = loginCredentialsSavedLocally
      ? this.props.loginTypeResult.secretKey
      : this.state.secretInputValue;
    let tmpRememberMe = loginCredentialsSavedLocally
      ? true
      : this.state.rememberMe;
    let tmpAccessInputValid = loginCredentialsSavedLocally
      ? true
      : this.state.accessInputValid;
    let tmpSecretInputValue = loginCredentialsSavedLocally
      ? true
      : this.state.secretInputValid;

    let tmpSubmitButtonEnabled = (loginCredentialsSavedLocally && this.state.pinInputValid) || this.isAllFieldsValid();

    return (
      <Modal isOpen={true} modalClassName="modal-dialog modal-dialog-centered ">
        <ModalHeader >
          <div >
            <div>
              <LoadingBar/>
            </div>
          </div>
          <i className="material-icons md-36">cloud_upload
          </i>{' '}s3ackup</ModalHeader>
        <ModalBody>

          <div>
            <Alert color="warning" isOpen={this.props.loginFailedAlertOpen}>
              {this.props.loginFailedAlertText}
            </Alert>
          </div>

          <Form >
            <FormGroup>
              <Input type="text" name="access" id="access" placeholder="AWS Access Key" valid={tmpAccessInputValid} onChange={this.accessOnChange} disabled={loginCredentialsSavedLocally} value={tmpAccessKey}/>
            </FormGroup>
            <FormGroup>
              <Input type="password" name="secret" id="secret" placeholder="AWS Secret Key" valid={tmpSecretInputValue} onChange={this.secretOnChange} disabled={loginCredentialsSavedLocally} value={tmpSecretKey}/>
            </FormGroup>
            <FormGroup check>
              <Label check>
                <Input type="checkbox" checked={tmpRememberMe} onChange={this.toggleRememberMe} disabled={loginCredentialsSavedLocally}/> {' '}
                Remember
              </Label>
            </FormGroup>
            {tmpRememberMe
              ? <FormGroup>
                  <Input type="password" name="pin" id="pin" placeholder="Pin" valid={this.state.pinInputValid} onChange={this.pinOnChange}/>
                  <UncontrolledTooltip placement="left" target="pin">
                    AWS Secret Key will be encypted and stored locally. This pin will be used to encrypt and later decrypt the key.
                  </UncontrolledTooltip>

                </FormGroup>
              : null}
          </Form>
        </ModalBody>
        <ModalFooter>
          <Button onClick={this.buttonLogin} disabled={!tmpSubmitButtonEnabled}>Login</Button>
          {loginCredentialsSavedLocally
            ? <Button onClick={this.clearLocalCredential}>Reset</Button>
            : null}
        </ModalFooter>
      </Modal>

    );
  }
}

function mapStateToProps(state) {
  let loginFailedAlertOpen = false;
  let loginFailedAlertText = "";
  if (state.login && state.login.success === false) {
    loginFailedAlertOpen = true;
    loginFailedAlertText = state.login.message;
  }
  return {loginTypeResult: state.loginType, login: state.login, loginFailedAlertOpen: loginFailedAlertOpen, loginFailedAlertText: loginFailedAlertText};
}

export default(connect(mapStateToProps, {login, loginType, clearLogin})(Login));
