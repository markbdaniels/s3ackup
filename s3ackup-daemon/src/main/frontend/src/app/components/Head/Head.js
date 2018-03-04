import React, {Component} from 'react';
import {connect} from "react-redux";
import LoadingBar from 'react-redux-loading-bar'
import {logout} from "../../../redux/actions";
import {
  Collapse,
  Navbar,
  NavbarToggler,
  NavbarBrand,
  Nav,
  NavItem,
  NavLink
} from 'reactstrap';

class Head extends Component {
  constructor(props) {
    super(props);
    this.state = {
      collapsed: true
    };
  }

  toggleNavbar = () => {
    this.setState({
      collapsed: !this.state.collapsed
    });
  }

  render() {
    return (
      <div >
        <div className="loading-container">
          <div>
            <LoadingBar/>
          </div>
        </div>

        <Navbar color="light" light>
          <NavbarBrand href="/" className="mr-auto">
            <i className="material-icons md-36">cloud_upload
            </i>{' '}s3ackup</NavbarBrand>
          <NavbarToggler onClick={this.toggleNavbar} className="mr-2"/>
          <Collapse isOpen={!this.state.collapsed} navbar>
            <Nav navbar>
              <NavItem>
                <NavLink href="#" onClick={this.props.onLogout}>logout</NavLink>
              </NavItem>
            </Nav>
          </Collapse>
        </Navbar>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {};
}

export default(connect(mapStateToProps, {logout})(Head));
