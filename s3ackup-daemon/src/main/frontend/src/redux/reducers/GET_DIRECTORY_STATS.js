import {GET_DIRECTORY_STATS_FULFILLED} from "../actions";

export default function(state = [], action) {
  switch (action.type) {
    case GET_DIRECTORY_STATS_FULFILLED:
      let key = action.payload.data.path;
      let value = action.payload.data;
      return {
        ...state,
        [key]: value
      }
    default:
      return state;
  }
}
