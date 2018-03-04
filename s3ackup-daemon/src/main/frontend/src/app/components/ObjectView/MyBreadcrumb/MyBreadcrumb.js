import React, {Component} from 'react';
import {Link} from "react-router-dom"
import {Breadcrumb, BreadcrumbItem} from 'reactstrap';

class MyBreadcrumb extends Component {

  trimPath(path, char = '/') {
    const escapedString = char.replace(/[[\](){}?*+^$\\.|-]/g, '\\$&');
    return path.replace(new RegExp(`^[ ${escapedString}]+|[ ${escapedString}]+$`, 'g'), '');
  }

  explodePath(path, pathSeparator) {
    const trimedPath = this.trimPath(path, pathSeparator);
    if (trimedPath === '') {
      return [];
    }
    return trimedPath.split(pathSeparator);
  }

  render() {
    let pathSplitArray = this.explodePath(this.props.path, '/');
    let pathObjArray = [];

    for (let i = 0; i < pathSplitArray.length; i++) {
      let name = pathSplitArray[i];
      let tmpPath = "";
      for (let j = 0; j <= i; j++) {
        tmpPath = tmpPath + "/" + pathSplitArray[j]
      }

      let pathObj = {
        name: name,
        path: tmpPath
      };
      pathObjArray.push(pathObj);
    }

    let out = pathObjArray.map(p => <BreadcrumbItem key={p.path}>
      <Link to={p.path} className="grey-anchor">{p.name}</Link>
    </BreadcrumbItem>);

    return (
      <div>
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={`/`} className="grey-anchor">
              s3ackup
            </Link>
          </BreadcrumbItem>
          {out}
        </Breadcrumb>
      </div>
    );
  }
}

export default MyBreadcrumb;
