import axios from "axios";
import GlobalConfig from './GlobalConfig';

const ROOT_URL = GlobalConfig.restApiBase + '/api';

// Login
export const LOGIN = "LOGIN";
export const LOGIN_FULFILLED = "LOGIN_FULFILLED";
const LOGIN_URL = ROOT_URL + '/login/submit'
export function login(payload, callback) {
  const request = axios.post(LOGIN_URL, payload).then(callback);
  return {type: LOGIN, payload: request};
}

// Logout
export const LOGOUT = "LOGOUT";
const LOGOUT_URL = ROOT_URL + '/login/logout'
export function logout(callback) {
  const request = axios.get(LOGOUT_URL).then(callback);
  return {type: LOGOUT, payload: request};
}

// Get Login type
export const LOGIN_TYPE = "LOGIN_TYPE";
export const LOGIN_TYPE_FULFILLED = "LOGIN_TYPE_FULFILLED";
const LOGIN_TYPE_URL = ROOT_URL + '/login/type'
export function loginType() {
  const request = axios.get(LOGIN_TYPE_URL);
  return {type: LOGIN_TYPE, payload: request};
}

// Clear local credential
const LOGIN_CLEAR_URL = ROOT_URL + '/login/clear'
export function clearLogin() {
  const request = axios.get(LOGIN_CLEAR_URL);
  return {type: LOGIN_TYPE, payload: request};
}

// Get the root directories
export const GET_ROOT_DIRS = "GET_ROOT_DIRS";
export const GET_ROOT_DIRS_FULFILLED = "GET_ROOT_DIRS_FULFILLED";
const GET_ROOT_DIRS_URL = ROOT_URL + '/getRootDirectories'
export function getRootDirs(errorCallback) {
  const request = axios.get(GET_ROOT_DIRS_URL).catch(errorCallback);
  return {type: GET_ROOT_DIRS, payload: request};
}

// Enable a root directory
export const ENABLE_ROOT_DIR = "ENABLE_ROOT_DIR";
export const ENABLE_ROOT_DIR_FULFILLED = "ENABLE_ROOT_DIR_FULFILLED";
const ENABLE_ROOT_DIR_URL = ROOT_URL + '/enableRootDirectory'
export function enableRootDirectory(rootArray) {
  let fullUrl = ENABLE_ROOT_DIR_URL + '?';
  for (let rootDir of rootArray) {
    fullUrl = fullUrl + '&root=' + rootDir;
  }
  const request = axios.post(fullUrl);
  return {type: ENABLE_ROOT_DIR, payload: request};
}

// Disable a root directory
export const DISABLE_ROOT_DIR = "DISABLE_ROOT_DIR";
export const DISABLE_ROOT_DIR_FULFILLED = "DISABLE_ROOT_DIR_FULFILLED";
const DISABLE_ROOT_DIR_URL = ROOT_URL + '/disableRootDirectory'
export function disableRootDirectory(rootArray) {
  let fullUrl = DISABLE_ROOT_DIR_URL + '?';
  for (let rootDir of rootArray) {
    fullUrl = fullUrl + '&root=' + rootDir;
  }
  const request = axios.post(fullUrl);
  return {type: DISABLE_ROOT_DIR, payload: request};
}

// Get file list
export const GET_FILE_LIST = "GET_FILE_LIST";
export const GET_FILE_LIST_FULFILLED = "GET_FILE_LIST_FULFILLED";
const GET_FILE_LIST_URL = ROOT_URL + '/getFileList?path='
export function getFileList(path, refresh = false, errorCallback) {
  let fullUrl = GET_FILE_LIST_URL + path;
  if (refresh) {
    fullUrl += "&refresh=true";
  }
  const request = axios.get(fullUrl).catch(errorCallback);;
  return {type: GET_FILE_LIST, payload: request};
}

// Get directory stats
export const GET_DIRECTORY_STATS = "GET_DIRECTORY_STATS";
export const GET_DIRECTORY_STATS_FULFILLED = "GET_DIRECTORY_STATS_FULFILLED";
const GET_DIRECTORY_STATS_URL = ROOT_URL + '/getDirectoryStats?path='
export function getDirectoryStats(path) {
  let fullUrl = GET_DIRECTORY_STATS_URL + path;
  const request = axios.get(fullUrl);
  return {type: GET_DIRECTORY_STATS, payload: request};
}

// Get directory stats
export const GET_DIRECTORIES_STATS = "GET_DIRECTORIES_STATS";
export const GET_DIRECTORIES_STATS_FULFILLED = "GET_DIRECTORIES_STATS_FULFILLED";
export const GET_DIRECTORIES_STATS_FOR_DELETE = "GET_DIRECTORIES_STATS_FOR_DELETE";
export const GET_DIRECTORIES_STATS_FOR_DELETE_FULFILLED = "GET_DIRECTORIES_STATS_FOR_DELETE_FULFILLED";
const GET_DIRECTORIES_STATS_URL = ROOT_URL + '/getDirectoriesStats?forceCalculatable=true&refresh=true'
export function getDirectoriesStats(pathArray, key = GET_DIRECTORIES_STATS) {
  let fullUrl = GET_DIRECTORIES_STATS_URL;
  for (let path of pathArray) {
    fullUrl += `&path=${path}`;
  }
  const request = axios.get(fullUrl);
  return {type: key, payload: request};
}

// Download files
export const DOWNLOAD_PATHS = "DOWNLOAD_PATHS";
const DOWNLOAD_PATHS_URL = ROOT_URL + '/downloadPaths'
export function downloadPaths(pathArray, callback) {
  let fullUrl = DOWNLOAD_PATHS_URL + '?';
  for (let path of pathArray) {
    fullUrl = fullUrl + '&path=' + path;
  }
  const request = axios.get(fullUrl).then(callback);;
  return {type: DOWNLOAD_PATHS, payload: request};
}

// delete files
export const DELETE_PATHS = "DELETE_PATHS";
const DELETE_PATHS_URL = ROOT_URL + '/deletePaths'
export function deletePaths(pathArray, callback) {
  let fullUrl = DELETE_PATHS_URL + '?';
  for (let path of pathArray) {
    fullUrl = fullUrl + '&path=' + path;
  }
  const request = axios.get(fullUrl).then(callback);
  return {type: DELETE_PATHS, payload: request};
}

// list tasks
export const LIST_TASKS = "LIST_TASKS";
export const LIST_TASKS_FULFILLED = "LIST_TASKS_FULFILLED";
const LIST_TASKS_URL = ROOT_URL + '/listTasks'
export function listTasks() {
  const request = axios.get(LIST_TASKS_URL);
  return {type: LIST_TASKS, payload: request};
}

// redirect
export const REDIRECT = "REDIRECT";
export function redirect(url) {
  return {type: REDIRECT, payload: url};
}
