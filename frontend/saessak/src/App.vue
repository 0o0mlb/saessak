<template>
    <RouterView></RouterView>
</template>

<script setup>
import {loginStore} from '@/store/loginStore';

const store = loginStore();
const {
	setlogout,
	setCurkid,
	setKidlist,
	setTeacherFlag,
	setTeachername,
    setClassroomname,
	setAlarmFlag,
} = store;
const lStore = JSON.parse(localStorage.getItem('loginStore'));
// console.log("loginStore : " + loginStore)
if(lStore == null){
    setlogout()
    setCurkid(-1)
    setKidlist()
    setTeacherFlag(false)
    setTeachername("")
    setClassroomname("")
    setAlarmFlag(false)
}
</script>

<style scoped></style>
