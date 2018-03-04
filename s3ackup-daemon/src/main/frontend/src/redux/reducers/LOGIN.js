import {LOGIN_FULFILLED} from "../actions";

export default function(state = [], action) {
  switch (action.type) {
    case LOGIN_FULFILLED:
      return action.payload.data;
    default:
      return state;
  }
}
