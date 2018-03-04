import React, {Component} from 'react';
import {Progress} from 'reactstrap';

class TaskQueueItem extends Component {

  render() {
    let out = <div/>;
    let task = this.props.cloudTask;
    let progressColor = task.progress === "complete"
      ? "success"
      : "info";
    let progressBar = <Progress value={task.progressPct} color={progressColor}/>;

    let toCapitalCase = require('to-capital-case')
    let type = toCapitalCase(task.cloudAction);

    out = <div className="card border-dark mb-2 ml-2 task-queue-item">
      <div className="card-header border-secondary">{type}</div>
      <div className="card-body">
        <h5 className="card-title">{progressBar}</h5>
        <p className="card-text">{task.cloudPath}</p>
      </div>
    </div>
    return out;
  }
}

export default TaskQueueItem;
