# Bdp Template

项目使用基于`vue3`的框架[FesJs](https://winixt.gitee.io/fesjs/zh/)以及[FesUI](https://fes-design.mumblefe.cn)，搭配`hooks`思想组合功能

`注：后台接口需要在开发网中访问`

## 本地开发：
```
wnpm i
npm run dev
```

## 跨域联调

使用`whistle`代理

在whistle规则中配置如下规则并启用，在浏览器访问`http://xx.xx.xx.xx:port/web/`既可以将html代理到本地开发环境，localhost的端口需要根据实际情况替换
```
http://xx.xx.xx.xx:port/web/	http://localhost:8000/
```

或者跨域启动浏览器（chrome 84版本及以下版本推荐）
- windows

    旧版-在桌面新增一个chrome的快捷方式并重命名，右键属性在`快捷方式-目标`输入框中最后加入参数`--disable-web-security --user-data-dir`，关闭chrome直接双击新建的快捷方式就可以跨域打开浏览器

    新版-在桌面新增一个chrome的快捷方式并重命名，右键属性在`快捷方式-目标`输入框中最后加入参数`--args --disable-web-security --user-data-dir=C:\MyChromeDevUserData`，MyChromeDevUserData目录可以自定义但一定是必须的，关闭chrome直接双击新建的快捷方式就可以跨域打开浏览器

- mac

    新建目录chormedata，并在终端执行命令`open -n /Applications/Google\ Chrome.app/ --args --disable-web-security  --user-data-dir=/Users/zhuguifeng/Documents/chromedata`即可跨域打开浏览器，其中新建的目录绝对路径放在--user-data-dir的参数中

注意，以上参数中的`--user-data-dir`必须根据实际情况修改

## 代码提交规范

每次提交代码都会经过`eslint`的校验，开发者需要处理报告中出现的错误

针对提交message也需要按照规范填写，方便后面回溯记录，规范如下：

`['upd', 'feat', 'fix', 'refactor', 'docs', 'chore', 'style', 'revert']: 提交信息`

## @fesjs/fes-design的使用

- 需要全局注入组件需要在`app.js`文件中引入并且`use`
- API使用的组件直接在对应页面按需引入即可，如：`import { FMessage } from '@fesjs/fes-design';`

## 项目结构

## svg filter generator

项目中使用到了一些svg图片，需要实现hover变色的效果，svg filter的生成器地址：[https://codepen.io/sosuke/pen/Pjoqqp](https://codepen.io/sosuke/pen/Pjoqqp)
