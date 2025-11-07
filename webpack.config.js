const path = require("path");
const CopyPlugin = require("copy-webpack-plugin");

module.exports = {
  mode: "development",
  entry: "./target/scala-2.13/scalajs-bundler/main/laminar-vega-demo-fastopt.js",
  devServer: {
    static: {
      directory: path.join(__dirname, "src/main/resources")
    },
    compress: true,
    port: 8080,
    hot: true,
    open: true
  },
  plugins: [
    new CopyPlugin({
      patterns: [
        { from: "src/main/resources", to: "" }
      ]
    })
  ]
};
