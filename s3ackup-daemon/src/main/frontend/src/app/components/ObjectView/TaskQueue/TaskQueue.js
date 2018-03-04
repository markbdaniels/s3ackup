import React, {Component} from 'react';
import {connect} from "react-redux";
import {listTasks} from "../../../../redux/actions";
import {CardBody, Card} from 'reactstrap';
import TaskQueueItem from "./TaskQueueItem";

class TaskQueue extends Component {

  componentDidMount() {
    this.props.listTasks();
  }

  render() {

    var out;
    if (this.props.cloudTasks && this.props.cloudTasks.length > 0) {
      out = this.props.cloudTasks.map(item => <TaskQueueItem key={item.id} cloudTask={item}/>);
    } else {
      out = <div>0 tasks found</div>;
    }

    return (
      <Card>
        <CardBody>
          <div className="row">
            {out}
          </div>
        </CardBody>
      </Card>
    );
  }
}

function mapStateToProps(state) {
  return {cloudTasks: state.cloudTasks};
}

export default(connect(mapStateToProps, {listTasks})(TaskQueue));
