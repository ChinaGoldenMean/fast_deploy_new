webpackJsonp([26],{"d6/B":function(t,e,a){"use strict";e.c=function(t){return Object(n.a)({url:"/billing/opoff/show",method:"get",params:t})},e.d=function(t){return Object(n.a)({url:"/billing/opoff/up",method:"post",data:t,transformRequest:[function(t){return t=s.a.stringify(t)}]})},e.b=function(t){return Object(n.a)({url:"/billing/env/show",method:"get",params:t})},e.a=function(t){return Object(n.a)({url:"/billing/op/hostnames",method:"get",params:t})},e.e=function(t){return Object(n.a)({url:"/billing/op/hostnames",method:"post",data:t})};var n=a("Vo7i"),o=a("mw3O"),s=a.n(o)},tJTI:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var n=a("d6/B"),o={name:"Config",data:function(){return{formDialog:!1,hostNameList:[{name:""}],formData:{},listLoading:!1,envSelect:[],queryParams:{centerId:""},rangeTime:"",configData:[],switchValue:"",dialogData:{type:"add",row:{}}}},methods:{addChildren:function(){this.hostNameList.push({name:""})},removeChildren:function(t){if(this.hostNameList.length>1){var e=this.hostNameList.indexOf(t);-1!==e&&this.hostNameList.splice(e,1)}},closeFormDialog:function(){this.dialogData={type:"add",row:{}},this.formDialog=!1,this.formData={},this.hostNameList=[{name:""}]},openDialog:function(t,e){var a=this;"update"==e&&(this.dialogData={type:"update",row:t},Object(n.a)({envId:t.envId}).then(function(t){var e=t.data.data;200===t.data.code?(a.hostNameList=[],e.forEach(function(t){a.hostNameList.push({name:t})}),a.formDialog=!0):a.$message({message:t.data.msg,type:"error"})}))},submit:function(){var t=this,e=new FormData;this.hostNameList.forEach(function(t){e.append("hostnames",t.name)}),e.append("envId",this.dialogData.row.envId),Object(n.e)(e).then(function(e){200===e.data.code?(t.$message({message:e.data.msg,type:"success"}),t.closeFormDialog()):t.$message({message:e.data.msg,type:"error"})})},searchEnvSelectChange:function(t){this.queryParams.envId=t,this.getList()},switchChange:function(t){var e=this,a=t.split(";"),o=a[0],s=a[1];Object(n.d)({envId:o,isAbleNext:s}).then(function(t){var a=t.data.code;200==a?e.$message({message:t.data.msg,type:"success"}):e.$message({message:t.data.msg,type:"error"}),200==a&&e.getList()})},getList:function(){var t=this;t.listLoading=!0,Object(n.c)(t.queryParams).then(function(e){var a=e.data.data,n=e.data.code;200===n?t.configData=a:"未查询到任何数据"!=e.data.msg&&201!=n||(t.configData=[]);setTimeout(function(){t.listLoading=!1},200)})},getEnv:function(){var t=this;Object(n.b)().then(function(e){200==e.data.code?(t.envSelect=e.data.data,0!=t.envSelect.length&&t.$nextTick(function(){t.queryParams.envId=t.envSelect[0].id,t.getList()})):t.envSelect=[]})}},created:function(){this.getEnv()}},s={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",[a("el-main",[a("el-row",{staticClass:"filter-container",attrs:{type:"flex"}},[a("span",{staticClass:"row-span-class"},[t._v("环境")]),t._v(" "),a("el-select",{staticStyle:{width:"10%"},attrs:{placeholder:"请选择",size:"small"},on:{change:t.searchEnvSelectChange},model:{value:t.queryParams.envId,callback:function(e){t.$set(t.queryParams,"envId",e)},expression:"queryParams.envId"}},t._l(t.envSelect,function(t){return a("el-option",{key:t.id,attrs:{label:t.envName,value:t.id}})}),1)],1),t._v(" "),a("el-card",{directives:[{name:"loading",rawName:"v-loading",value:t.listLoading,expression:"listLoading"}]},t._l(t.configData,function(e,n){return a("el-row",{key:n,staticStyle:{width:"90%","border-bottom":"1px solid #dcdfe6",padding:"10px"},attrs:{gutter:5}},[a("el-col",{attrs:{span:5}},[t._v("环境Code："+t._s(e.envCode))]),t._v(" "),a("el-col",{attrs:{span:7}},[t._v("上次操作时间："+t._s(e.opTime))]),t._v(" "),a("el-col",{attrs:{span:5}},[t._v("上次操作用户："+t._s(e.opUserId))]),t._v(" "),t.$store.getters.authCheck("yx_config_env_operation")?a("el-col",{attrs:{span:3}},[a("el-switch",{attrs:{value:e.envId+";"+e.isAbleNext,"active-value":e.envId+";1","inactive-value":e.envId+";0","active-text":"开启","inactive-text":"关闭"},on:{change:t.switchChange}})],1):t._e(),t._v(" "),a("el-col",{attrs:{span:4}},[t.$store.getters.authCheck("yx_config_hostname")?a("el-button",{staticClass:"button-m-small",attrs:{size:"mini",type:"primary"},on:{click:function(a){return t.openDialog(e,"update")}}},[t._v("更新HostName\n          ")]):t._e()],1)],1)}),1),t._v(" "),a("el-dialog",{attrs:{visible:t.formDialog,title:"更新HostName",top:"5vh","close-on-click-modal":t.elDialogCloseOnClickModal,"destroy-on-close":t.elDialogDestroyOnClose},on:{"update:visible":function(e){t.formDialog=e},close:t.closeFormDialog}},[a("el-form",{ref:"addModule",staticStyle:{width:"100%","max-height":"70vh","overflow-y":"auto","overflow-x":"hidden",padding:"0 5px"},attrs:{model:t.formData,"label-position":"left","label-width":"100px"}},t._l(t.hostNameList,function(e,n){return a("el-row",{key:n,attrs:{gutter:20}},[a("el-col",{attrs:{span:16}},[a("el-form-item",{attrs:{label:"HostName"}},[a("el-input",{model:{value:e.name,callback:function(a){t.$set(e,"name",a)},expression:"item.name"}})],1)],1),t._v(" "),a("el-col",{attrs:{span:8}},[a("el-button",{attrs:{icon:"el-icon-minus"},on:{click:function(a){return t.removeChildren(e)}}}),t._v(" "),a("el-button",{attrs:{icon:"el-icon-plus"},on:{click:function(e){return t.addChildren()}}})],1)],1)}),1),t._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:t.closeFormDialog}},[t._v("关闭")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:t.submit}},[t._v("确定")])],1)],1)],1)],1)},staticRenderFns:[]};var i=a("VU/8")(o,s,!1,function(t){a("yxpB")},"data-v-62f35e94",null);e.default=i.exports},yxpB:function(t,e){}});