import React, {Component} from 'react';
import {connect} from "react-redux";
import {getDirectoriesStats} from "../../../../redux/actions";
import {ClipLoader} from 'react-spinners';

class FileStats extends Component {

  render() {
    let path = this.props.path;
    let out = <ClipLoader size={20}/>;

    if (this.props.directoriesStatsForDelete) {
      let stats;
      for (let tmpObj of this.props.directoriesStatsForDelete) {
        if (tmpObj.path === path) {
          stats = tmpObj;
          break;
        }
      }
      if (stats) {
        let objText = stats.count > 1
          ? "objects"
          : "object";
        const prettyBytes = require('pretty-byte');
        let sizePretty = prettyBytes(stats.size);
        out = <span>{stats.count} {' '}{objText}
          - {sizePretty}</span>
      }
    }

    return (
      <span>
        <table>
          <tbody>
            <tr>
              <td>{path}:</td>
            </tr>
            <tr>
              <td>{out}<hr/></td>
            </tr>
          </tbody>
        </table>
      </span>
    );
  }
}

function mapStateToProps(state) {
  return {directoriesStatsForDelete: state.directoriesStatsForDelete};
}

export default(connect(mapStateToProps, {getDirectoriesStats})(FileStats));
