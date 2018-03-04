import {LIST_TASKS_FULFILLED} from "../actions";

export default function(state = [], action) {
  switch (action.type) {
    case LIST_TASKS_FULFILLED:
      return action.payload.data.taskResponseList;
    default:
      return state;
  }
}
