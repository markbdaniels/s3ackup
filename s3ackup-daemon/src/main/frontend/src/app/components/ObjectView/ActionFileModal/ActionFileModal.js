import React from 'react';
import {connect} from "react-redux";
import {Button, Modal, ModalHeader, ModalBody, ModalFooter} from 'reactstrap';
import {GET_DIRECTORIES_STATS_FOR_DELETE, getDirectoriesStats} from "../../../../redux/actions";
import FileStats from './FileStats';

class ActionFileModal extends React.Component {

  componentDidMount() {
    let pathSet = this.props.getPathsCallback();
    if (pathSet && pathSet.length > 0) {
      this.props.getDirectoriesStats(pathSet, GET_DIRECTORIES_STATS_FOR_DELETE);
    }
  }

  render() {
    let pathSet = this.props.getPathsCallback();
    let out = pathSet.map(item => <div key={item} className="row"><FileStats path={item}/></div>);

    return (
      <div>
        <Modal isOpen={this.props.isOpen}>
          <ModalHeader >{this.props.headerLabel}</ModalHeader>
          <ModalBody>
            {out}
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={this.props.acceptCallback}>{this.props.submitButtonLabel}</Button>{' '}
            <Button color="secondary" onClick={this.props.declineCallback}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {directoriesStatsForDelete: state.directoriesStatsForDelete};
}

export default(connect(mapStateToProps, {getDirectoriesStats})(ActionFileModal));
