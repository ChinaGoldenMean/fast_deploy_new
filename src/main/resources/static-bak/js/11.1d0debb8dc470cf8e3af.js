webpackJsonp([11],{"+MLA":function(e,t,n){var r=n("EqjI"),a=n("06OY").onFreeze;n("uqUo")("freeze",function(e){return function(t){return e&&r(t)?e(a(t)):t}})},"70P3":function(e,t,n){"use strict";t.a=function(e){0;return e}},"87o/":function(e,t){},Cdx3:function(e,t,n){var r=n("sB3e"),a=n("lktj");n("uqUo")("keys",function(){return function(e){return a(r(e))}})},DYnl:function(e,t,n){"use strict";var r=n("Xxa5"),a=n.n(r),o=n("exGp"),l=n.n(o),u=n("xDXP"),s=n("doyg"),i=n("70P3"),c={props:{display:{type:String,required:!1,default:function(){return""}},name:{type:String,required:!1,default:function(){return""}},id:{type:String,required:!1,default:function(){return""}}},setup:function(e,t){var n={envID:-1};return{options:Object(u.d)([]),propConfig:{lazy:!0,lazyLoad:function(e,t){var r=this;return l()(a.a.mark(function o(){var l,u,c,p,d,m,f,v,b,h;return a.a.wrap(function(r){for(;;)switch(r.prev=r.next){case 0:l=e.level,Object(i.a)("级联懒加载层："+l),r.t0=l,r.next=0===r.t0?5:1===r.t0?11:2===r.t0?19:27;break;case 5:return r.next=7,Object(s.b)();case 7:return u=r.sent,c=u.map(function(e){return{value:e.id,label:e.envName,leaf:l>=2}}),t(c),r.abrupt("break",28);case 11:return p=e.value,n.envID=p,r.next=15,Object(s.a)(p);case 15:return d=r.sent,m=d.map(function(e){return{value:e.id,label:e.centerName,leaf:l>=2}}),t(m),r.abrupt("break",28);case 19:return f=e.value,r.next=22,Object(s.d)(n.envID,f);case 22:return v=r.sent,b=v.list,h=b.map(function(e){return{value:[e.moduleId,e.moduleName],label:e.moduleName,leaf:l>=2}}),t(h),r.abrupt("break",28);case 27:return r.abrupt("break",28);case 28:case"end":return r.stop()}},o,r)}))()}},selected:function(e){t.emit("change",[e[2][0],e[2][1]]),t.emit("update:id",e[2][0].toString()),t.emit("update:name",e[2][1])}}}},p={render:function(){var e=this,t=e.$createElement;return(e._self._c||t)("el-cascader",{staticClass:"custom-cascader",attrs:{options:e.options,props:e.propConfig,"show-all-levels":!1,placeholder:0===e.display.length?"请选择模块":e.display},on:{change:e.selected},model:{value:e.display,callback:function(t){e.display=t},expression:"display"}})},staticRenderFns:[]};var d=n("VU/8")(c,p,!1,function(e){n("87o/")},"data-v-62540328",null);t.a=d.exports},O4R0:function(e,t,n){n("+MLA"),e.exports=n("FeBl").Object.freeze},fZjL:function(e,t,n){e.exports={default:n("jFbC"),__esModule:!0}},jFbC:function(e,t,n){n("Cdx3"),e.exports=n("FeBl").Object.keys},u2KI:function(e,t,n){e.exports={default:n("O4R0"),__esModule:!0}},uqUo:function(e,t,n){var r=n("kM2E"),a=n("FeBl"),o=n("S82l");e.exports=function(e,t){var n=(a.Object||{})[e]||Object[e],l={};l[e]=t(n),r(r.S+r.F*o(function(){n(1)}),"Object",l)}},wUez:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r=n("zL8q"),a=n("u2KI"),o=n.n(a),l=o()({1:"UI自动化",2:"接口自动化"}),u=o()({1:"String",2:"Number",3:"Boolean"}),s={props:{form:{type:Object,required:!0}},setup:function(){return{TYPE:l,PARAMETERS_TYPE:u}}},i={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-form",{attrs:{size:"mini"}},[n("el-row",[n("el-col",{attrs:{span:11}},[n("el-form-item",{attrs:{label:"名称",required:"","label-width":"110px"}},[n("el-input",{model:{value:e.form.name,callback:function(t){e.$set(e.form,"name",t)},expression:"form.name"}})],1)],1),e._v(" "),n("el-col",{attrs:{span:11}},[n("el-form-item",{attrs:{label:"分类",required:"","label-width":"110px"}},[n("el-select",{model:{value:e.form.type,callback:function(t){e.$set(e.form,"type",t)},expression:"form.type"}},e._l(e.TYPE,function(e,t,r){return n("el-option",{key:r,attrs:{value:t,label:e}})}),1)],1)],1)],1),e._v(" "),n("el-row",[n("el-col",{attrs:{span:11}},[n("el-form-item",{attrs:{label:"参数类型",required:"","label-width":"110px"}},[n("el-select",{model:{value:e.form.paramType,callback:function(t){e.$set(e.form,"paramType",t)},expression:"form.paramType"}},e._l(e.PARAMETERS_TYPE,function(e,t,r){return n("el-option",{key:r,attrs:{value:t,label:e}})}),1)],1)],1),e._v(" "),n("el-col",{attrs:{span:11}},[n("el-form-item",{attrs:{label:"参数描述",required:"","label-width":"110px"}},[n("el-input",{model:{value:e.form.paramValue,callback:function(t){e.$set(e.form,"paramValue",t)},expression:"form.paramValue"}})],1)],1)],1),e._v(" "),n("el-row",[n("el-col",{attrs:{span:11}},[n("el-form-item",{attrs:{label:"备注",required:"","label-width":"110px"}},[n("el-input",{attrs:{type:"textarea",autosize:""},model:{value:e.form.remark,callback:function(t){e.$set(e.form,"remark",t)},expression:"form.remark"}})],1)],1)],1)],1)},staticRenderFns:[]},c=n("VU/8")(s,i,!1,null,null,null).exports,p={components:{ModuleSelector:n("DYnl").a},props:{list:{type:Array,required:!0}},setup:function(e){return{appendItem:function(){e.list.push({moduleId:-1,name:"",key:"",value:"",remark:""})},deleteRow:function(t){e.list.splice(t,1)}}}},d={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-table",{attrs:{data:e.list}},[n("el-table-column",{attrs:{align:"center",label:"环境"},scopedSlots:e._u([{key:"default",fn:function(t){var r=t.row;return[n("module-selector",{attrs:{display:r.name,id:r.moduleId,name:r.name},on:{"update:id":function(t){return e.$set(r,"moduleId",t)},"update:name":function(t){return e.$set(r,"name",t)}}})]}}])}),e._v(" "),n("el-table-column",{attrs:{align:"center",label:"键"},scopedSlots:e._u([{key:"default",fn:function(t){var r=t.row;return[n("el-input",{attrs:{size:"mini"},model:{value:r.key,callback:function(t){e.$set(r,"key",t)},expression:"row.key"}})]}}])}),e._v(" "),n("el-table-column",{attrs:{align:"center",label:"值"},scopedSlots:e._u([{key:"default",fn:function(t){var r=t.row;return[n("el-input",{attrs:{size:"mini"},model:{value:r.value,callback:function(t){e.$set(r,"value",t)},expression:"row.value"}})]}}])}),e._v(" "),n("el-table-column",{attrs:{align:"center",label:"备注"},scopedSlots:e._u([{key:"default",fn:function(t){var r=t.row;return[n("el-input",{attrs:{size:"mini"},model:{value:r.remark,callback:function(t){e.$set(r,"remark",t)},expression:"row.remark"}})]}}])}),e._v(" "),n("el-table-column",{attrs:{width:"50px"},scopedSlots:e._u([{key:"default",fn:function(t){var r=t.$index;return[n("el-link",{attrs:{type:"danger",underline:!1},on:{click:function(t){return e.deleteRow(r)}}},[n("i",{staticClass:"el-icon-delete"})])]}}])},[n("template",{slot:"header"},[n("el-link",{attrs:{type:"primary",underline:!1},on:{click:e.appendItem}},[n("i",{staticClass:"el-icon-plus"})])],1)],2)],1)},staticRenderFns:[]};var m,f,v,b,h,w={components:{BasicForm:c,EnvParamList:n("VU/8")(p,d,!1,null,null,null).exports},props:{show:{type:Boolean,required:!0},data:{type:Object,required:!0}},setup:function(e,t){return{close:function(e){r.MessageBox.confirm("确认要退出编辑?","提示",{type:"warning"}).then(function(){"function"==typeof e&&e(),t.emit("close")}).catch()},submit:function(){var n,a,o,l,u,s;n=e.data,a=n.name,o=n.paramType,l=n.paramValue,u=n.type,s=void 0,a&&0!==a.trim().length||(s="名称不能为空"),o||(s="参数类型不能为空"),l&&0!==l.trim().length||(s="参数描述不能为空"),u||(s="分类不能为空"),function(e){for(var t=0;t<e.length;t++)0!==e[t].key.trim().length&&0!==e[t].name.trim().length||e.splice(t,1)}(n.envVars),(!s||(r.Message.warning(s),0))&&t.emit("submit")}}}},x={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-dialog",{attrs:{title:"📝公共参数编辑器",visible:e.show,width:"50%",top:"5%","before-close":e.close}},[n("el-row",[n("basic-form",{attrs:{form:e.data}})],1),e._v(" "),n("el-row",[n("env-param-list",{attrs:{list:e.data.envVars}})],1),e._v(" "),n("span",{attrs:{slot:"footer"},slot:"footer"},[n("el-button",{on:{click:e.close}},[e._v("取 消")]),e._v(" "),n("el-button",{attrs:{type:"primary"},on:{click:e.submit}},[e._v("确 认")])],1)],1)},staticRenderFns:[]},y=n("VU/8")(w,x,!1,null,null,null).exports,g=n("fZjL"),k=n.n(g),_=n("Xxa5"),S=n.n(_),T=n("exGp"),j=n.n(T),O=n("xDXP"),P=n("tV+W"),E=n("MERw"),V=(m=j()(S.a.mark(function e(t,n){var r,a,o=arguments.length>2&&void 0!==arguments[2]?arguments[2]:10,l=arguments.length>3&&void 0!==arguments[3]?arguments[3]:{asc:!0,column:"name"};return S.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return r={current:t,name:n,size:o,order:l},e.next=3,P.a.post("/param/listByName",r);case 3:return a=e.sent,Object(E.b)(a),e.abrupt("return",a.result);case 6:case"end":return e.stop()}},e,this)})),function(e,t){return m.apply(this,arguments)}),I=(f=j()(S.a.mark(function e(t){var n;return S.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:if(t.id){e.next=2;break}return e.abrupt("return");case 2:return e.next=4,P.a.post("/param/saveOrUpdate",t);case 4:return n=e.sent,e.abrupt("return",Object(E.b)(n));case 6:case"end":return e.stop()}},e,this)})),function(e){return f.apply(this,arguments)}),R=(v=j()(S.a.mark(function e(t){var n;return S.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:if(!t.id){e.next=2;break}return e.abrupt("return");case 2:return e.next=4,P.a.post("/param/saveOrUpdate",t);case 4:return n=e.sent,e.abrupt("return",Object(E.b)(n));case 6:case"end":return e.stop()}},e,this)})),function(e){return v.apply(this,arguments)}),$=(b=j()(S.a.mark(function e(t){var n;return S.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:if(t){e.next=2;break}return e.abrupt("return");case 2:return e.next=4,P.a.delete("/param/deleteById?id="+t);case 4:return n=e.sent,e.abrupt("return",Object(E.b)(n));case 6:case"end":return e.stop()}},e,this)})),function(e){return b.apply(this,arguments)}),A=(h=j()(S.a.mark(function e(t){var n;return S.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:if(t){e.next=2;break}return e.abrupt("return");case 2:return e.next=4,P.a.get("/param/selectOne?id="+t);case 4:return n=e.sent,Object(E.b)(n),e.abrupt("return",n.result);case 7:case"end":return e.stop()}},e,this)})),function(e){return h.apply(this,arguments)}),L=function(){return{name:"",paramType:"1",paramValue:"",remark:"",type:"2",envVars:[]}};var q={components:{CommonParamEditor:y},setup:function(){var e=function(){var e=this,t=Object(O.d)([]),n=Object(O.d)(0),a=Object(O.d)(1),o=Object(O.d)(""),l=Object(O.d)(!0);Object(O.c)(j()(S.a.mark(function r(){var u,s,i;return S.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,V(a.value,o.value);case 2:u=e.sent,s=u.data,i=u.totalItem,t.value=s,n.value=i,l.value=!1;case 8:case"end":return e.stop()}},r,e)}))),Object(O.f)(a,j()(S.a.mark(function r(){var u,s,i;return S.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return l.value=!0,e.next=3,V(a.value,o.value);case 3:u=e.sent,s=u.data,i=u.totalItem,t.value=s,n.value=i,l.value=!1;case 9:case"end":return e.stop()}},r,e)})));var u=setTimeout(function(){},555);return Object(O.f)(o,function(){l.value=!0,clearTimeout(u),u=setTimeout(function(){return V(a.value,o.value).then(function(e){var r=e.totalItem,a=e.data;t.value=a,n.value=r,l.value=!1})},555)}),{commonParamList:t,commonParamTotal:n,currentPage:a,searchText:o,isLoad:l,changePage:function(e){return a.value=e},deleteRow:function(e,n){var a=this;this.$confirm("确认要删除 '"+e.name+"' 这个参数?",{title:"Delete",confirmButtonText:"删除",cancelButtonText:"取消",type:"warning"}).then(j()(S.a.mark(function o(){return S.a.wrap(function(a){for(;;)switch(a.prev=a.next){case 0:return a.next=2,$(e.id);case 2:if(!a.sent){a.next=5;break}r.Notification.success(e.name+"删除成功。"),t.value.splice(n,1);case 5:case"end":return a.stop()}},o,a)})))}}}(),t=e.commonParamList,n=e.commonParamTotal,a=e.currentPage,o=e.searchText,s=e.isLoad,i=e.changePage,c=e.deleteRow,p=function(){var e,t=(e=j()(S.a.mark(function e(){return S.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,I(n.value.target);case 2:if(!e.sent){e.next=6;break}k()(n.value.ref).forEach(function(e){n.value.ref[e]=n.value.target[e]}),r.Notification.success("更新成功."),n.value.show=!1;case 6:case"end":return e.stop()}},e,this)})),function(){return e.apply(this,arguments)}),n=Object(O.d)({show:!1,target:{},ref:{}});return{edit:n,openEditor:function(e){n.value.ref=e,A(e.id).then(function(e){e.type=e.type.toString(),e.paramType=e.paramType.toString(),e.envVars=e.envVars||[];for(var t=0;t<e.envVars.length;t++)e.envVars[t].moduleId=e.envVars[t].moduleId||"",e.envVars[t].name=e.envVars[t].name||"",e.envVars[t].key=e.envVars[t].key||"",e.envVars[t].remark=e.envVars[t].remark||"",e.envVars[t].value=e.envVars[t].value||"",e.envVars[t].moduleId=e.envVars[t].moduleId.toString();n.value.target=e,n.value.show=!0})},closeEditor:function(){n.value.show=!1},updateSubmit:t}}(),d=p.edit,m=p.openEditor,f=p.closeEditor,v=p.updateSubmit,b=function(){var e,t=(e=j()(S.a.mark(function e(){return S.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,R(n.value.target);case 2:if(!e.sent){e.next=7;break}n.value.show=!1,n.value.target={},r.Notification.success("新增成功."),location.reload();case 7:case"end":return e.stop()}},e,this)})),function(){return e.apply(this,arguments)}),n=Object(O.d)({show:!1,target:{}});return{add:n,openAddit:function(){n.value.target=L(),n.value.show=!0},closeAddit:function(){n.value.show=!1,n.value.target={}},insertSubmit:t}}(),h=b.add,w=b.openAddit,x=b.closeAddit,y=b.insertSubmit;return{commonParamList:t,commonParamTotal:n,currentPage:a,searchText:o,isLoad:s,edit:d,add:h,PARAMETERS_TYPE:u,TYPE:l,changePage:i,deleteRow:c,openEditor:m,closeEditor:f,updateSubmit:v,openAddit:w,closeAddit:x,insertSubmit:y}}},z={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("el-row",{staticStyle:{padding:"10px"}},[n("el-col",{attrs:{span:2}},[n("el-button",{attrs:{type:"primary",icon:"el-icon-magic-stick"},on:{click:e.openAddit}},[e._v("\n        添加\n      ")])],1),e._v(" "),n("el-col",{attrs:{offset:17,span:5}},[n("el-input",{attrs:{"prefix-icon":"el-icon-search"},model:{value:e.searchText,callback:function(t){e.searchText=t},expression:"searchText"}})],1)],1),e._v(" "),n("el-row",[n("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.isLoad,expression:"isLoad"}],staticStyle:{width:"100%"},attrs:{data:e.commonParamList,"highlight-current-row":!0}},[n("el-table-column",{attrs:{width:"200px",align:"center",label:"名称",prop:"name"}}),e._v(" "),n("el-table-column",{attrs:{width:"133px",align:"center",label:"分类",prop:"type"},scopedSlots:e._u([{key:"default",fn:function(t){var n=t.row;return[e._v("\n          "+e._s(e.TYPE[n.type])+"\n        ")]}}])}),e._v(" "),n("el-table-column",{attrs:{align:"center",label:"备注",prop:"remark"}}),e._v(" "),n("el-table-column",{attrs:{width:"133px",align:"center",label:"创建者",prop:"createBy"}}),e._v(" "),n("el-table-column",{attrs:{align:"center",label:"更新时间",prop:"updateTime"},scopedSlots:e._u([{key:"default",fn:function(t){var n=t.row;return[e._v("\n          "+e._s(new Date(n.updateTime).toLocaleString().replaceAll("/","-"))+"\n        ")]}}])}),e._v(" "),n("el-table-column",{attrs:{align:"center",label:"创建时间",prop:"createTime"},scopedSlots:e._u([{key:"default",fn:function(t){var n=t.row;return[e._v("\n          "+e._s(new Date(n.updateTime).toLocaleString().replaceAll("/","-"))+"\n        ")]}}])}),e._v(" "),n("el-table-column",{attrs:{align:"center",label:"操作",width:"200"},scopedSlots:e._u([{key:"default",fn:function(t){var r=t.row,a=t.$index;return[n("el-button",{attrs:{size:"mini",type:"primary",icon:"el-icon-edit"},on:{click:function(t){return e.openEditor(r)}}},[e._v("\n            编辑\n          ")]),e._v(" "),n("el-button",{attrs:{size:"mini",type:"danger",icon:"el-icon-delete"},on:{click:function(t){return e.deleteRow(r,a)}}},[e._v("\n            删除\n          ")])]}}])})],1),e._v(" "),n("el-pagination",{staticStyle:{margin:"20px"},attrs:{background:"","current-page":e.currentPage,layout:"prev, pager, next",total:e.commonParamTotal},on:{"current-change":e.changePage}})],1),e._v(" "),n("common-param-editor",{attrs:{show:e.edit.show,data:e.edit.target},on:{"update:show":function(t){return e.$set(e.edit,"show",t)},close:e.closeEditor,submit:e.updateSubmit}}),e._v(" "),n("common-param-editor",{attrs:{show:e.add.show,data:e.add.target},on:{"update:show":function(t){return e.$set(e.add,"show",t)},close:e.closeAddit,submit:e.insertSubmit}})],1)},staticRenderFns:[]},B=n("VU/8")(q,z,!1,null,null,null);t.default=B.exports}});