webpackJsonp([23],{"+fAZ":function(e,a,t){"use strict";a.c=function(e){return Object(s.a)({url:"/permission/getAllPermission",method:"get",params:e})},a.a=function(e){return Object(s.a)({url:"/permission/insert",method:"post",data:e})},a.d=function(e){return Object(s.a)({url:"/permission/update",method:"post",data:e})},a.b=function(e){return Object(s.a)({url:"/permission/delete",method:"post",data:e})};var s=t("Vo7i")},is86:function(e,a){},lKKt:function(e,a,t){"use strict";a.d=function(e){return Object(s.a)({url:"/base/permission/all",method:"get",params:e})},a.g=function(e){return Object(s.a)({url:"/base/permission/updateOne",method:"post",data:e})},a.b=function(e){return Object(s.a)({url:"/base/permission/bindListBasePerm",method:"post",data:e})},a.a=function(e){return Object(s.a)({url:"/base/permission/addOne",method:"post",data:e})},a.e=function(e){return Object(s.a)({url:"/base/permission/getOne?basePermissionId="+e,method:"get"})},a.f=function(e){return Object(s.a)({url:"/base/permission/getBaseBindPermList?basePermissionId="+e,method:"get"})},a.c=function(e){return Object(s.a)({url:"/base/permission/delBasePerm",method:"post",params:e})};var s=t("Vo7i")},tUNF:function(e,a,t){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var s=t("ifoU"),i=t.n(s),r=t("lKKt"),o=t("+fAZ"),l={permission:o.c,base:r.d},n={name:"cuser",data:function(){return{name:"base",tableData:[],tableDataPermission:[],gridData:[],listLoading:!1,queryParams:{keyName:"",beginTime:"",endTime:"",isActive:"",depId:"",pageNum:1,pageSize:10},queryParams1:{keyName:"",beginTime:"",endTime:"",isActive:"",depId:"",pageNum:1,pageSize:10},queryParamsAll:{keyName:"",beginTime:"",endTime:"",isActive:"",depId:"",pageNum:1,pageSize:1e4},pages:1,total:1,pages1:1,total1:1,dialogData:{type:"add",row:{}},formData:{perNames:[],basePerCode:"",basePerName:"",remark:""},detailsDialog:!1,formDialogPermission:!1,formDialog:!1,updataSelected:[],AllgridData:[]}},mounted:function(){this.getList(),this.getListGri()},methods:{getRowKey:function(e){return e.id},handleSelectionChange:function(e){},selectSubmit:function(){var e=this.$refs.multipleTableBase.selection;this.formData.perNames=this.unique(e),console.log(this.formData.perNames),this.formDialogPermission=!1,this.closeFormDialog("permission")},unique:function(e){var a=new i.a;return e.filter(function(e){return!a.has(e.id)&&a.set(e.id,1)})},handleSizeChange:function(e){this.queryParams.pageSize=e,this.getList()},handleCurrentChange:function(e){this.queryParams.pageNum=e,this.getList()},handleSizeChange1:function(e){this.queryParams1.pageSize=e,this.getListGri()},handleCurrentChange1:function(e){this.queryParams1.pageNum=e,this.getListGri()},getList:function(){var e=this;e.listLoading=!0,l[this.name](this.queryParams).then(function(a){var t=a.data.data,s=a.data.code;if(200===s)"permission"==e.name||(e.tableData=t.list),e.total=t.total,e.pages=t.pages,e.queryParams.pageNum=t.pageNum,e.queryParams.pageSize=t.pageSize;else{if("未查询到任何数据"==a.data.msg||201==s)return e.tableData=[],e.total=0,void(e.listLoading=!1);200!=s&&(e.tableData=[],e.total=0,e.listLoading=!1)}setTimeout(function(){e.listLoading=!1},200)})},getListGri:function(){var e=this,a=this;Object(o.c)(this.queryParams1).then(function(t){var s=t.data.data,i=t.data.code;200===i?(a.gridData=s.list,a.total1=s.total,a.pages1=s.pages,e.queryParams1.pageNum=s.pageNum,e.queryParams1.pageSize=s.pageSize,a.$nextTick(function(){for(var e=a.updataSelected,t=0;t<e.length;t++)for(var s=0;s<a.AllgridData.length;s++)e[t].id===a.AllgridData[s].id&&a.$refs.multipleTableBase.toggleRowSelection(a.AllgridData[s],!0)})):"未查询到任何数据"!=t.data.msg&&201!=i||(a.gridData=[]);setTimeout(function(){},200)})},openDialog:function(e,a){var t=this;if("add"==a&&(this.formDialog=!0,this.dialogData={type:"add",row:{title:"新增基础权限"}}),"detail"==a&&Object(r.e)(e.id).then(function(a){var s=a.data.code;a.data.data;if(200===s)t.dialogData={type:"detail",row:e},t.detailsDialog=!0;else{var i=a.data.msg;t.$message({message:i,type:"error"})}}),"update"==a){var s=this;Object(r.f)(e.id).then(function(a){t.updataSelected=a.data.data;var i=a.data.code,r=a.data.data;if(200===i)s.dialogData.type="update",s.formData=e,s.formData.perNames=r,s.formDialog=!0;else{var o=a.data.msg;s.$message({message:o,type:"error"})}})}if("delete"==a&&this.$confirm("确定要删除选中的资源吗?","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(function(){t.resourceDelete(e)}).catch(function(){t.$message({type:"info",message:"已取消"})}),"permission"==a){var i=this;i.name="permission",i.formDialogPermission=!0,Object(o.c)(i.queryParamsAll).then(function(e){var a=e.data.data;200===e.data.code&&(i.AllgridData=a.list)}),i.getListGri(),i.handleCurrentChange1(1)}},closeDetailsDialog:function(){this.dialogData={type:"add",row:{}},this.detailsDialog=!1},closeFormDialog:function(e){if("base"==e&&(this.dialogData={type:"add",row:{}},this.formDialog=!1,this.formData={perNames:[],basePerCode:"",basePerName:"",remark:""}),"permission"==e){var a=this;a.name="base",a.formDialogPermission=!1,a.$nextTick(function(){void 0==a.$refs.multipleTableBase&&a.$refs.multipleTableBase==[]&&""==a.$refs.multipleTableBase&&null==a.$refs.multipleTableBase||a.$refs.multipleTableBase.clearSelection()})}},permissionCheck:function(e){var a=[];return void 0!=this.envPermission&&this.envPermission.permissionSet&&(a=this.envPermission.permissionSet),a.includes(e)},submit:function(){var e=this,a=new FormData;a.append("basePerCode",this.formData.basePerCode),a.append("basePerName",this.formData.basePerName),a.append("remark",this.formData.remark),"update"==this.dialogData.type&&a.append("id",this.formData.id);var t=this;("add"==this.dialogData.type?r.a:r.g)(a).then(function(a){if(200==a.data.code){if("update"==e.dialogData.type){var s=new FormData,i=t.formData.perNames.map(function(e){return e.id});s.append("permIds",i),s.append("basePermissionId",t.formData.id),Object(r.b)(s).then(function(a){var t=200==a.data.code?"success":"error";e.$message({message:a.data.msg,type:t})})}else t.$message({message:a.data.msg,type:"success"});t.getList(),t.formDialog=!1,t.closeFormDialog()}else t.$message({message:a.data.msg,type:"error"})})},resourceDelete:function(e){var a=this;Object(r.c)({basePermissionId:e.id}).then(function(e){200==e.data.code?(a.getList(),a.$message({message:e.data.msg,type:"success"})):a.$message({message:e.data.msg,type:"error"})})}},components:{}},c={render:function(){var e=this,a=e.$createElement,t=e._self._c||a;return t("div",[t("el-main",[t("el-row",{staticClass:"filter-container",attrs:{type:"flex"}},[t("el-input",{staticClass:"input-class",attrs:{clearable:"",placeholder:"输入信息"},model:{value:e.queryParams.keyName,callback:function(a){e.$set(e.queryParams,"keyName",a)},expression:"queryParams.keyName"}}),e._v(" "),t("el-button",{staticClass:"span-other",attrs:{icon:"el-icon-search",type:"primary"},on:{click:e.getList}},[e._v("查询")]),e._v(" "),t("el-button",{attrs:{icon:"el-icon-edit",size:"mini",type:"primary"},on:{click:function(a){return e.openDialog({},"add")}}},[e._v("新增")])],1),e._v(" "),t("el-card",[t("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.listLoading,expression:"listLoading"}],ref:"appTable",staticStyle:{width:"100%"},attrs:{data:e.tableData,"element-loading-text":"拼命加载中...","highlight-current-row":!0}},[t("el-table-column",{attrs:{align:"center",label:"序号",type:"index",width:"100"},scopedSlots:e._u([{key:"default",fn:function(a){return[e._v("\n            "+e._s(a.$index+1+e.queryParams.pageSize*(e.queryParams.pageNum-1))+"\n          ")]}}])}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"基础权限名称",prop:"basePerName"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"基础权限",prop:"basePerCode"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"创建时间",prop:"createTime"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"修改时间",prop:"updateTime"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"操作",width:"300"},scopedSlots:e._u([{key:"default",fn:function(a){return[t("el-button",{staticClass:"button-m-small",attrs:{size:"mini",type:"success"},on:{click:function(t){return e.openDialog(a.row,"detail")}}},[e._v("详情\n            ")]),e._v(" "),t("el-button",{staticClass:"button-m-small",attrs:{size:"mini",type:"primary"},on:{click:function(t){return e.openDialog(a.row,"update")}}},[e._v("修改\n            ")]),e._v(" "),t("el-button",{staticClass:"button-m-small",attrs:{size:"mini",type:"danger"},on:{click:function(t){return e.openDialog(a.row,"delete")}}},[e._v("删除\n            ")])]}}])})],1)],1),e._v(" "),t("el-footer",[t("div",{staticClass:"pagination-container"},[t("el-pagination",{attrs:{"current-page":e.queryParams.pageNum,"page-count":e.pages,"page-size":e.queryParams.pageSize,"page-sizes":[5,10,15,30,50],total:e.total,background:"",layout:"total, sizes, prev, pager, next, jumper"},on:{"current-change":e.handleCurrentChange,"size-change":e.handleSizeChange}})],1)]),e._v(" "),t("el-dialog",{attrs:{visible:e.detailsDialog,title:"基础权限详情",top:"5vh","close-on-click-modal":e.elDialogCloseOnClickModal,"destroy-on-close":e.elDialogDestroyOnClose},on:{"update:visible":function(a){e.detailsDialog=a},close:e.closeDetailsDialog}},[t("el-row",{staticClass:"details-row",attrs:{gutter:10,justify:"space-between",type:"flex"}},[t("el-col",{attrs:{span:12}},[t("span",{staticClass:"details-span"},[e._v("基础权限名称：")]),t("span",[e._v(e._s(e.dialogData.row.basePerName))])])],1),e._v(" "),t("el-row",{staticClass:"details-row",attrs:{gutter:10,justify:"space-between",type:"flex"}},[t("el-col",{attrs:{span:12}},[t("span",{staticClass:"details-span"},[e._v("基础权限code：")]),t("span",[e._v(e._s(e.dialogData.row.basePerCode))])])],1),e._v(" "),t("el-row",{staticClass:"details-row",attrs:{gutter:10,justify:"space-between",type:"flex"}},[t("el-col",{attrs:{span:12}},[t("span",{staticClass:"details-span"},[e._v("备注：")]),t("span",[e._v(e._s(e.dialogData.row.remark))])])],1),e._v(" "),t("el-row",{staticClass:"details-row",attrs:{gutter:10,justify:"space-between",type:"flex"}},[t("el-col",{attrs:{span:12}},[t("span",{staticClass:"details-span"},[e._v("创建时间：")]),t("span",[e._v(e._s(e.dialogData.row.createTime))])])],1),e._v(" "),t("el-row",{staticClass:"details-row",attrs:{gutter:10,justify:"space-between",type:"flex"}},[t("el-col",{attrs:{span:12}},[t("span",{staticClass:"details-span"},[e._v("修改时间：")]),t("span",[e._v(e._s(e.dialogData.row.updateTime))])])],1),e._v(" "),t("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[t("el-button",{attrs:{type:"primary"},on:{click:e.closeDetailsDialog}},[e._v("关闭")])],1)],1),e._v(" "),t("el-dialog",{attrs:{visible:e.formDialog,top:"10vh","close-on-click-modal":e.elDialogCloseOnClickModal,"destroy-on-close":e.elDialogDestroyOnClose,customClass:"customWidth"},on:{"update:visible":function(a){e.formDialog=a},close:e.closeFormDialog}},[t("el-form",{ref:"addModule",staticStyle:{width:"100%","max-height":"70vh","overflow-y":"auto","overflow-x":"hidden",padding:"0 5px"},attrs:{model:e.formData,"label-position":"left","label-width":"100px"}},[t("el-row",{attrs:{gutter:24}},[t("el-col",{attrs:{span:12}},[t("div",[t("el-form-item",{attrs:{label:"基础权限名称：",prop:"basePerName"}},[t("el-input",{model:{value:e.formData.basePerName,callback:function(a){e.$set(e.formData,"basePerName",a)},expression:"formData.basePerName"}})],1)],1)])],1),e._v(" "),t("el-row",{attrs:{gutter:24}},[t("el-col",{attrs:{span:12}},[t("el-form-item",{attrs:{label:"基础权限code：",prop:"basePerCode"}},[t("el-input",{model:{value:e.formData.basePerCode,callback:function(a){e.$set(e.formData,"basePerCode",a)},expression:"formData.basePerCode"}})],1)],1)],1),e._v(" "),"add"!=e.dialogData.type?[t("el-row",{attrs:{gutter:24}},[t("el-col",{attrs:{span:20}},[t("el-form",[t("el-form-item",{attrs:{label:"绑定权限名:",prop:"formData.perNames"}},e._l(e.formData.perNames,function(a){return t("el-tag",{key:a.prop},[e._v(e._s(a.actionName))])}),1)],1)],1),e._v(" "),t("el-button",{attrs:{plain:""},on:{click:function(a){return e.openDialog("","permission")}}},[e._v("点击选择")])],1)]:e._e(),e._v(" "),t("el-row",{attrs:{gutter:24}},[t("el-col",{attrs:{span:12}},[t("el-form-item",{attrs:{label:"备注：",prop:"remark"}},[t("el-input",{model:{value:e.formData.remark,callback:function(a){e.$set(e.formData,"remark",a)},expression:"formData.remark"}})],1)],1)],1)],2),e._v(" "),t("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[t("el-button",{on:{click:function(a){return e.closeFormDialog("base")}}},[e._v("关闭")]),e._v(" "),t("el-button",{attrs:{type:"primary"},on:{click:e.submit}},[e._v("确定")])],1)],1),e._v(" "),t("el-dialog",{attrs:{visible:e.formDialogPermission},on:{"update:visible":function(a){e.formDialogPermission=a}}},[t("el-row",{staticClass:"filter-container",attrs:{type:"flex"}},[t("el-input",{staticClass:"input-class",attrs:{clearable:"",placeholder:"输入信息"},model:{value:e.queryParams1.keyName,callback:function(a){e.$set(e.queryParams1,"keyName",a)},expression:"queryParams1.keyName"}}),e._v(" "),t("el-button",{staticClass:"span-other",attrs:{icon:"el-icon-search",type:"primary"},on:{click:e.getListGri}},[e._v("查询")])],1),e._v(" "),t("el-card",[t("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.listLoading,expression:"listLoading"}],ref:"multipleTableBase",staticStyle:{width:"100%"},attrs:{data:e.gridData,"element-loading-text":"拼命加载中...","row-key":e.getRowKey,"highlight-current-row":!0},on:{"selection-change":e.handleSelectionChange}},[t("el-table-column",{attrs:{fixed:"",type:"selection","reserve-selection":!0,width:"55"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"权限定义中文名",prop:"actionName"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"权限定义字符串",prop:"action"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"创建时间",prop:"createTime"}}),e._v(" "),t("el-table-column",{attrs:{align:"center",label:"修改时间",prop:"updateTime"}})],1)],1),e._v(" "),t("el-footer",[t("div",{staticClass:"pagination-container"},[t("el-pagination",{attrs:{"current-page":e.queryParams1.pageNum,"page-size":e.queryParams1.pageSize,"page-sizes":[5,10,15,30,50],total:e.total1,background:"",layout:"total, sizes, prev, pager, next, jumper"},on:{"current-change":e.handleCurrentChange1,"size-change":e.handleSizeChange1}})],1)]),e._v(" "),t("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[t("el-button",{on:{click:function(a){return e.closeFormDialog("permission")}}},[e._v("关闭")]),e._v(" "),t("el-button",{attrs:{type:"primary"},on:{click:e.selectSubmit}},[e._v("确定")])],1)],1)],1)],1)},staticRenderFns:[]};var m=t("VU/8")(n,c,!1,function(e){t("is86")},"data-v-58ad1d5a",null);a.default=m.exports}});