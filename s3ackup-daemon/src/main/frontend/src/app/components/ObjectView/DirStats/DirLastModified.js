import React, {Component} from 'react';
import {connect} from "react-redux";
import {ClipLoader} from 'react-spinners';
import {getDirectoryStats} from "../../../../redux/actions";

class DirLastModified extends Component {

  componentDidMount() {
    this.props.getDirectoryStats(this.props.row.path);
  }

  render() {
    let out = <ClipLoader size={20}/>;
    if (this.props.directoryStats) {
      let dirStats = this.props.directoryStats[this.props.row.path];
      if (dirStats) {
        if (dirStats.calculatable) {
          let moment = require('moment');
          out = moment(dirStats.dtLastModified).format(this.props.dtFormat);
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

export default(connect(mapStateToProps, {getDirectoryStats})(DirLastModified));
