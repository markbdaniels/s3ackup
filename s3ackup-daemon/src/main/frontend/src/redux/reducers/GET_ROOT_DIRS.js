import {GET_ROOT_DIRS_FULFILLED} from "../actions";
import {ENABLE_ROOT_DIR_FULFILLED} from "../actions";
import {DISABLE_ROOT_DIR_FULFILLED} from "../actions";

export default function(state = [], action) {
  switch (action.type) {
    case GET_ROOT_DIRS_FULFILLED:
      if (action.payload) {
        return action.payload.data.rootDirectoryList;
      }
      return state;
    case ENABLE_ROOT_DIR_FULFILLED:
      return action.payload.data.rootDirectoryList;
    case DISABLE_ROOT_DIR_FULFILLED:
      return action.payload.data.rootDirectoryList;
    default:
      return state;
  }
}
