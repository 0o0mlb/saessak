
<template>
  <!-- <div
    class="flex flex-col justify-between w-full h-screen max-w-md p-8 mx-auto my-auto overflow-y-scroll bg-white border border-gray-200 rounded-lg shadow scrollbar-hide sm:p-8 dark:bg-gray-800 dark:border-gray-700"
  >
    <component :is="chatSwitch"></component>
    <div class="fixed bottom-0 left-0 right-0 w-full max-w-md p-2 mx-auto bg-yellow-50">
      <div class="flex items-center justify-evenly">
        <button @click="showChat(ChatPersonView)">
            학부모목록
        </button>
        <button @click="showChat(ChatListView)">
            채팅목록
        </button>
      </div>
    </div>
  </div> -->
</template>

<script setup>
import { onMounted, shallowRef, ref } from "vue";
import ChatListView from "./ChatListView.vue";
import ChatPersonView from "./ChatPersonView.vue";

const chatSwitch = shallowRef(ChatPersonView);

const props = defineProps({
  size : {
    type : Object,
    default : () => ({
      width : 0,
      height : 0
    })
  }
})
onMounted(() => {
  // console.log("사이즈 받아왔니")
  // console.log(props.size)
})

const showChat = (name) => {
  chatSwitch.value = name;
  // console.log(chatSwitch.value);
};
</script>
<style scoped>

</style>
