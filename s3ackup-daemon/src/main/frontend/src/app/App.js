import React, {Component} from 'react';
import BucketList from './components/BucketView/BucketList';
import ObjectList from './components/ObjectView/ObjectList';
import Login from './components/Login/Login'

import {BrowserRouter, Route, Switch} from "react-router-dom";

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      collapsed: true
    };
  }

  toggleNavbar = () => {
    this.setState({
      collapsed: !this.state.collapsed
    });
  }

  render() {
    return (
      <div >
        <div >
          <BrowserRouter forceRefresh>
            <Switch>
              <Route path="/login" component={Login} exact/>
              <Route path="/" component={BucketList} exact/>
              <Route path="/*" component={ObjectList}/>
            </Switch>
          </BrowserRouter>
        </div>

      </div>
    );
  }
}

export default App;
