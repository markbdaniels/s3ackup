import React from 'react';
import ReactDOM from 'react-dom';

import App from './app/App';

import {Provider} from "react-redux";
import {createStore, applyMiddleware} from "redux";
import thunkMiddleware from 'redux-thunk'
import promiseMiddleware from 'redux-promise-middleware'
import {loadingBarMiddleware} from 'react-redux-loading-bar'
import reducers from "./redux/reducers";
// import {createLogger} from 'redux-logger'

const createStoreWithMiddleware = applyMiddleware(thunkMiddleware, promiseMiddleware(), loadingBarMiddleware()
// , createLogger()
)(createStore);

ReactDOM.render(
  <Provider store={createStoreWithMiddleware(reducers)}>
  <App/>
</Provider>, document.getElementById('root'));
