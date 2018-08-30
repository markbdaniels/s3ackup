import React, {Component} from "react";
import {connect} from "react-redux";
import {Link} from "react-router-dom";
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table';
import {getFileList} from "../../../redux/actions";
import {downloadPaths} from "../../../redux/actions";
import {deletePaths} from "../../../redux/actions";
import {listTasks} from "../../../redux/actions";
import {Alert, UncontrolledTooltip, Badge, Button, Collapse} from 'reactstrap';

import DirSize from "./DirStats/DirSize";
import DirStorage from "./DirStats/DirStorage";
import DirCount from "./DirStats/DirCount";
import DirLastModified from "./DirStats/DirLastModified";
import ActionFileModal from './ActionFileModal/ActionFileModal';
import TaskQueue from "./TaskQueue/TaskQueue";
import Head from '../Head/Head';
import MyBreadcrumb from "./MyBreadcrumb/MyBreadcrumb";

class ObjectList extends Component {

  constructor(props) {
    super(props);
    this.state = {
      deleteModal: false,
      downloadModal: false,
      selectedFiles: [],
      downloadingAlert: false,
      downloadingAlertFileCount: 0,
      deletingAlert: false,
      deletingAlertFileCount: 0,
      taskQueueCollapse: false
    };
  }

  componentDidMount() {
    this.props.getFileList(this.props.location.pathname, false, () => {
      this.props.history.push("/login");
    });
    this.timer = setInterval(this.queryTasksPoller, 3000);
  }

  componentWillUnmount() {
    clearInterval(this.timer);
  }

  queryTasksPoller = () => {
    this.props.listTasks();
  }

  /* Delete action stuff*/
  toggleDeleteModal = (newValue) => {
    let tmp = !this.state.deleteModal;
    if (newValue) {
      tmp = newValue;
    }
    this.setState({deleteModal: tmp});
  }

  deleteModalAcceptCallback = () => {
    let pathArray = this.getSelectedTableRows();
    this.props.deletePaths(pathArray, (response) => {
      this.setState({deletingAlert: "true", deletingAlertFileCount: response.data});
      this.queryTasksPoller();
    });
    this.toggleDeleteModal(false);
  }

  deleteModalDeclineCallback = () => {
    this.toggleDeleteModal(false);
  }

  onDismissDeletingAlert = () => {
    this.setState({deletingAlert: false});
  }

  /* download action stuff*/
  toggleDownloadModal = (newValue) => {
    let tmp = !this.state.downloadModal;
    if (newValue) {
      tmp = newValue;
    }
    this.setState({downloadModal: tmp});
  }

  downloadModalAcceptCallback = () => {
    let pathArray = this.getSelectedTableRows();
    this.props.downloadPaths(pathArray, (response) => {
      this.setState({downloadingAlert: true, downloadingAlertFileCount: response.data});
      this.queryTasksPoller();
    });

    this.toggleDownloadModal(false);
  }

  downloadModalDeclineCallback = () => {
    this.toggleDownloadModal(false);
  }

  onDismissDownloadingAlert = () => {
    this.setState({downloadingAlert: false});
  }

  getSelectedTableRows = () => {
    let checkedFilesArray = this.refs.filesTable.state.selectedRowKeys;
    let pathArray = [];
    for (let file of checkedFilesArray) {
      let absoluteFile = `${this.props.location.pathname}/${file}`;
      pathArray.push(absoluteFile);
    }
    return pathArray;
  }

  toggleTaskQueueCollapse = () => {
    this.setState({
      taskQueueCollapse: !this.state.taskQueueCollapse
    });
  }

  handleActionButton = (event) => {
    let pathArray = this.getSelectedTableRows();
    if (pathArray.length > 0) {
      switch (event.target.id) {
        case "downloadButtonId":
          this.toggleDownloadModal(true);
          break;
        case "deleteButtonId":
          this.toggleDeleteModal(true);
          break;
        default:
      }
    }
  }

  handleRefreshButton = (event) => {
    this.props.getFileList(this.props.location.pathname, true);
  }

  customFileNameFormat(cell, row) {
    let isDirectory = row.type === 'Directory';

    let icon = "folder";
    if (!isDirectory) {
      icon = "file_download";
      switch (row.syncStatus) {
        case "CLOUD_ONLY":
          icon = "cloud_download";
          break;
        case "HASH_EQUAL":
          icon = "cloud_done";
          break;
        case "HASH_NOT_EQUAL":
          icon = "cloud_off";
          break;
        default:
      }
    }

    let iconClazz = "material-icons  md-24 material-icons-folder";
    let iconToolTip = "Folder";
    if (!isDirectory) {
      iconClazz = "material-icons md-18 material-icons-file ";
      switch (row.syncStatus) {
        case "CLOUD_ONLY":
          iconClazz += "lightgrey";
          iconToolTip = "Object in S3 only";
          break;
        case "HASH_EQUAL":
          iconClazz += "green";
          iconToolTip = "Object in S3 and local file synchronized";
          break;
        case "HASH_NOT_EQUAL":
          iconClazz += "red";
          iconToolTip = "Object in S3 and local file inconsistent";
          break;
        default:
      }
    }

    let linkClazz = isDirectory
      ? "btn grey-anchor"
      : "btn grey-anchor disabled";

    let iconId = `iconId${cell.replace(/[^a-zA-Z0-9]/g, "")}`;

    return (
      <span>
        <i id={iconId} className={iconClazz}>{icon}
        </i>
        <Link to={`/${row.path}`} className={linkClazz}>
          {cell}
        </Link>
        <UncontrolledTooltip placement="right" target={iconId}>
          {iconToolTip}
        </UncontrolledTooltip>
      </span>
    );
  }

  customDateFormat(cell, row) {
    let dtFormat = 'MMM Do YYYY, HH:mm:ss';
    let isFile = row.type === 'File';
    if (isFile) {
      let moment = require('moment');
      return moment(cell).format(dtFormat);
    } else {
      return <DirLastModified row={row} dtFormat={dtFormat}/>
    }
  }

  customSizeFormat(cell, row) {
    let isFile = row.type === 'File';
    if (isFile) {
      const prettyBytes = require('pretty-byte');
      return prettyBytes(cell);
    } else {
      return <DirSize row={row}/>
    }

  }

  customStorageClassFormat(cell, row) {
    let isFile = row.type === 'File';
    if (isFile) {
      return cell;
    } else {
      return <DirStorage row={row}/>
    }
  }

  customCountClassFormat(cell, row) {
    let isFile = row.type === 'File';
    if (isFile) {
      return cell;
    } else {
      return <DirCount row={row}/>
    }
  }

  render() {
    let fileTableData = this.props.fileList;

    let deleteActionFileModal;
    if (this.state.deleteModal) {
      deleteActionFileModal = <ActionFileModal isOpen={this.state.deleteModal} headerLabel="Delete Objects?" submitButtonLabel="Delete" acceptCallback={this.deleteModalAcceptCallback} declineCallback={this.deleteModalDeclineCallback} getPathsCallback={this.getSelectedTableRows}/>;
    }

    let downloadActionFileModal;
    if (this.state.downloadModal) {
      downloadActionFileModal = <ActionFileModal isOpen={this.state.downloadModal} headerLabel="Download Objects?" submitButtonLabel="Download" acceptCallback={this.downloadModalAcceptCallback} declineCallback={this.downloadModalDeclineCallback} getPathsCallback={this.getSelectedTableRows}/>;
    }

    let collapseButton = "";
    if (this.props.cloudTasksSize > 0) {
      collapseButton = <div className="col-9">
        <Button color="primary" outline size="lg" onClick={this.toggleTaskQueueCollapse}>Task Queue{' '}
          <Badge color="primary">{this.props.cloudTasksSize}</Badge>
        </Button>
        <Collapse isOpen={this.state.taskQueueCollapse}>
          <TaskQueue/>
        </Collapse>
      </div>
    }

    return (
      <div>
        <Head onLogout={() => {
          this.props.history.push("/login")
        }}/>
        <div className="container-fluid ">

          <div className="row lead">
            <div className="col-sm-12">
              <MyBreadcrumb path={this.props.location.pathname}/>
            </div>
          </div>

          <div>
            <Alert color="dark" isOpen={this.state.downloadingAlert} toggle={this.onDismissDownloadingAlert}>
              Downloading{' '}{this.state.downloadingAlertFileCount}{' '}
              objects
            </Alert>
          </div>

          <div>
            <Alert color="dark" isOpen={this.state.deletingAlert} toggle={this.onDismissDeletingAlert}>
              Deleting{' '}{this.state.deletingAlertFileCount}{' '}
              objects
            </Alert>
          </div>

          <div className="row">

            <div className="col">
              <div className="dropdown">
                <button className="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenu2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  Action
                </button>
                <div className="dropdown-menu" aria-labelledby="dropdownMenu2">
                  <button id="downloadButtonId" className="dropdown-item" type="button" onClick={this.handleActionButton}>Download</button>
                  <div className="dropdown-divider"></div>
                  <button id="deleteButtonId" className="dropdown-item" type="button" onClick={this.handleActionButton}>Delete</button>
                </div>
              </div>
            </div>

            {collapseButton}

            <div className="col-1 float-right">
              <button id="refreshButton" className="btn btn-light btn-sm" type="button" onClick={this.handleRefreshButton}>
                <i className="material-icons md-36">refresh</i>
              </button>
            </div>
          </div>

          <div className="row">
            <BootstrapTable ref='filesTable' data={fileTableData} selectRow={{
              mode: 'checkbox'
            }} options={{
              noDataText: ' '
            }} bordered={false} hover>
              <TableHeaderColumn dataField='name' isKey dataFormat={this.customFileNameFormat} dataSort={true}>Name</TableHeaderColumn>
              <TableHeaderColumn dataField='dtLastModified' dataFormat={this.customDateFormat} dataSort={true}>Modified</TableHeaderColumn>
              <TableHeaderColumn dataField='count' dataFormat={this.customCountClassFormat} dataSort={true}>Count</TableHeaderColumn>
              <TableHeaderColumn dataField='size' dataFormat={this.customSizeFormat} dataSort={true}>Size</TableHeaderColumn>
              <TableHeaderColumn dataField='storageType' dataFormat={this.customStorageClassFormat} dataSort={true}>Storage Class</TableHeaderColumn>
            </BootstrapTable>
          </div>

          {deleteActionFileModal}
          {downloadActionFileModal}

        </div>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {fileList: state.fileList, cloudTasksSize: state.cloudTasksSize};
}

export default(connect(mapStateToProps, {getFileList, downloadPaths, deletePaths, listTasks})(ObjectList));
