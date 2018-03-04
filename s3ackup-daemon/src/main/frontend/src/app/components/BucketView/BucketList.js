import React, {Component} from "react";
import {connect} from "react-redux";
import {getRootDirs} from "../../../redux/actions";
import {enableRootDirectory} from "../../../redux/actions";
import {disableRootDirectory} from "../../../redux/actions";
import {Link} from "react-router-dom";
import Head from '../Head/Head';
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table';

class BucketList extends Component {

  componentDidMount() {
    this.props.getRootDirs(() => {
      this.props.history.push("/login");
    });
  }

  customRootFormat(cell, row) {
    let isEnabled = row.enabled;
    let clazzName = isEnabled === true
      ? "btn enabled grey-anchor"
      : "btn disabled grey-anchor";
    return (
      <span>
        <i className="material-icons">cloud
        </i>
        <Link to={`/${cell}`} className={clazzName}>
          {cell}
        </Link>
      </span>
    );
  }

  customDateFormat(cell, row) {
    let moment = require('moment');
    return moment(cell).format('MMM Do YYYY, h:mm:ss a ZZ');
  }

  handleActionButton = (event) => {
    let rootArray = this.refs.rootsTable.state.selectedRowKeys;
    switch (event.target.id) {
      case "enableButtonId":
        this.props.enableRootDirectory(rootArray);
        break;
      case "disableButtonId":
        this.props.disableRootDirectory(rootArray);
        break;
      default:
    }
  }

  render() {
    let rootTableBody = this.props.rootDirectoryList;
    return (
      <div>
        <Head onLogout={() => {
          this.props.history.push("/login")
        }}/>

        <div className="container-fluid">

          <div className="row">
            <div className="dropdown">
              <button className="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenu2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                Actions
              </button>
              <div className="dropdown-menu" aria-labelledby="dropdownMenu2">
                <button id="enableButtonId" className="dropdown-item" type="button" onClick={this.handleActionButton}>Enable Bucket</button>
                <button id="disableButtonId" className="dropdown-item" type="button" onClick={this.handleActionButton}>Disable Bucket</button>
              </div>
            </div>
          </div>

          <div className="row">
            <BootstrapTable ref='rootsTable' data={rootTableBody} selectRow={{
              mode: 'checkbox'
            }} options={{
              noDataText: ' '
            }} bordered={false} hover>
              <TableHeaderColumn dataField='name' isKey dataFormat={this.customRootFormat} dataSort={true}>Bucket</TableHeaderColumn>
              <TableHeaderColumn dataField='dtCreated' dataFormat={this.customDateFormat} dataSort={true}>Created</TableHeaderColumn>
            </BootstrapTable>
          </div>
        </div>

      </div>
    );
  }
}

function mapStateToProps(state) {
  return {rootDirectoryList: state.rootDirectoryList};
}

export default(connect(mapStateToProps, {getRootDirs, enableRootDirectory, disableRootDirectory})(BucketList));
