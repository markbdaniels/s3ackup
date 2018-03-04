import React, {Component} from 'react';
import {connect} from "react-redux";
import {ClipLoader} from 'react-spinners';
import {getDirectoryStats} from "../../../../redux/actions";

class DirStorage extends Component {

  componentDidMount() {
    this.props.getDirectoryStats(this.props.row.path);
  }

  render() {
    let out = <ClipLoader size={20}/>;
    if (this.props.directoryStats) {
      let dirStats = this.props.directoryStats[this.props.row.path];
      if (dirStats) {
        if (dirStats.calculatable) {
          let tmpStorage = "";
          for (let storageName in dirStats.countByStorageType) {
            tmpStorage += storageName + " : " + dirStats.countByStorageType[storageName] + " ";
          }
          out = tmpStorage;
        } else {
          out = "---";
        }
      }
    }
    return (
      <span>
        {out}
      </span>
    );
  }
}

function mapStateToProps(state) {
  return {directoryStats: state.directoryStats};
}

export default(connect(mapStateToProps, {getDirectoryStats})(DirStorage));
