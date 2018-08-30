var path = require('path');
const webpack = require('webpack');

module.exports = {

  entry: './src/index.js',
  output: {
    path: __dirname,
    filename: '../resources/static/built/bundle.js'
  },

  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: [/node_modules/],
        use: {
          loader: "babel-loader",
          options: {
            presets: ['es2015', 'es2016', 'stage-0', 'react']
          }
        }
      }
    ]
  }
};
