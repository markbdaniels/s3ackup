import {GET_DIRECTORIES_STATS_FOR_DELETE_FULFILLED} from "../actions";

export default function(state = [], action) {
  switch (action.type) {
    case GET_DIRECTORIES_STATS_FOR_DELETE_FULFILLED:
      return action.payload.data;
    default:
      return state;
  }
}
