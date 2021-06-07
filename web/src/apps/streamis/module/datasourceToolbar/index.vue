<template>
  <div class="designer-toolbar">
    <div class="button">
      <Icon type="md-settings" />
      <span>配置</span>
    </div>
    <div class="devider" />
    <div class="button">
      <Icon type="md-play" />
      <span>停止</span>
    </div>
    <div class="devider" />
    <div class="button">
      <svg
        class="icon"
        viewBox="0 0 1024 1024"
        version="1.1"
        xmlns="http://www.w3.org/2000/svg"
        p-id="4564"
      >
        <path
          d="M176.64 1024c-97.28 0-176.64-80.384-176.64-178.688V178.688c0-98.816 79.36-178.688 176.64-178.688h670.72c97.28 0 176.64 80.384 176.64 178.688V845.312c0 98.816-79.36 178.688-176.64 178.688h-670.72z m0-936.96c-50.688 0-91.648 41.472-91.648 92.16V845.312c0 50.688 40.96 92.16 91.648 92.16h670.72c50.688 0 91.648-41.472 91.648-92.16V178.688c0-50.688-40.96-92.16-91.648-92.16h-3.584v437.248h-663.04v-437.248h-4.096z m581.632 350.208v-350.208h-492.544v350.208h492.544z m-160.768-35.328v-240.128h84.992v240.128h-84.992z"
          p-id="4565"
        ></path>
      </svg>
      <span>保存</span>
    </div>
    <div class="devider" />
    <div class="button">
      <Icon type="md-swap" />
      <span>切换SQL</span>
    </div>
  </div>
</template>

<script>
export default {

}
</script>

<style lang="scss">
.designer-toolbar {
    height: 40px;
    padding-left: 10px;
    background: #f7f7f7;
    color: #000;
    border: 1px solid #d7dde4;
    .button {
        float: left;
        margin: 6px 5px 6px 0;
        padding: 0;
        height: 18px;
        text-align: left;
        border: 1px solid transparent;
        border-radius: 2px;
        cursor: pointer;
     svg {
        margin-right: 5px;
        color: #666;
      }
     span {
          vertical-align: middle;
          color: #666;
      }
     .icon {
          display: inline-block;
          vertical-align: text-top;
          width: 18px;
          height: 18px;
          color: #333;
          text-align: center;
      }
    }
    .devider {
        float: left;
        border-left: 1px solid #e3e8ee;
        height: 18px;
        margin: 8px 5px;
      }
    }
</style>