class GlobalConfig {
  constructor() {
    this.restApiBase = document.getElementById('baseApi').content;
  }
}
export default(new GlobalConfig());
