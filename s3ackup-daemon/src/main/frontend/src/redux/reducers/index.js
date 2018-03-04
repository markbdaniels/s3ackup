import {combineReducers} from "redux";
import GetRootDirsReducer from "./GET_ROOT_DIRS";
import GetFileListReducer from "./GET_FILE_LIST";
import GetDirectoryStatsReducer from "./GET_DIRECTORY_STATS";
import ListTasksReducer from "./LIST_TASKS";
import GetTaskSizeReducer from "./GET_TASK_SIZE";
import GetDirectoriesStatsForDelete from "./GET_DIRECTORIES_STATS_FOR_DELETE";
import Login from "./LOGIN";
import LoginType from "./LOGIN_TYPE";

import {loadingBarReducer} from 'react-redux-loading-bar'

const rootReducer = combineReducers({
  login: Login,
  loginType: LoginType,
  loadingBar: loadingBarReducer,
  rootDirectoryList: GetRootDirsReducer,
  fileList: GetFileListReducer,
  directoryStats: GetDirectoryStatsReducer,
  cloudTasks: ListTasksReducer,
  cloudTasksSize: GetTaskSizeReducer,
  directoriesStatsForDelete: GetDirectoriesStatsForDelete
});

export default rootReducer;
