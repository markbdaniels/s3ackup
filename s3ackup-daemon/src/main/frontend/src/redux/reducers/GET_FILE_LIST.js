import {GET_FILE_LIST_FULFILLED} from "../actions";

export default function(state = [], action) {
  switch (action.type) {
    case GET_FILE_LIST_FULFILLED:
      return action.payload.data.fileList;
    default:
      return state;
  }
}
