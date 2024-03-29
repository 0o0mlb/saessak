<template>
	<div class="view-frame">
		<vue-cal
			class="mx-5 my-3"
			active-view="month"
			hide-view-selector
			:disable-views="['years', 'year', 'week', 'day']"
			show-all-day-events="true"
			events-on-month-view="true"
			:events="events"
			:locale="'ko'"
			startWeekOnSunday
		>
			<template #title="{ view }">
				<span v-if="view.id === 'month'">{{
					view.startDate.format('YYYY년 MMMM')
				}}</span>
			</template>
		</vue-cal>
	</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAttendanceStore } from '@/store/attendance';

const store = useAttendanceStore();

const oneKidList = ref([]);

const events = ref([]);

const loginStore = JSON.parse(localStorage.getItem('loginStore'));

const kidId = loginStore.kidList[0].kidId;

// 내 아이의 출석부 비동기 호출
const getList = async () => {
	await store.getOneKidList(kidId);
	oneKidList.value = store.oneKidList;

	// vue-cal에 입력할 events 형식으로 변환
	for (let i = 0; i < oneKidList.value.length; i++) {
		// 결석
		if (!oneKidList.value[i].attendanceInTime) {
			const noEvent = {
				start: oneKidList.value[i].attendanceDate,
				end: oneKidList.value[i].attendanceDate,
				content: '결석',
				class: 'no-event',
				allDay: true,
			};
			events.value.push(noEvent);
		} else {
			// 하원 미입력
			if (!oneKidList.value[i].attendanceOutTime) {
				const inEvent = {
					start: oneKidList.value[i].attendanceDate,
					end: oneKidList.value[i].attendanceDate,
					content: `등원 ${oneKidList.value[i].attendanceInTime}`,
					class: 'in-event',
					allDay: true,
				};
				events.value.push(inEvent);

				const falseEvent = {
					start: oneKidList.value[i].attendanceDate,
					end: oneKidList.value[i].attendanceDate,
					content: '하원: 미입력',
					class: 'false-event',
					allDay: true,
				};
				events.value.push(falseEvent);
			}
			// 정상 등하원
			else {
				const inEvent = {
					start: oneKidList.value[i].attendanceDate,
					end: oneKidList.value[i].attendanceDate,
					content: `등원 ${oneKidList.value[i].attendanceInTime}`,
					class: 'in-event',
					allDay: true,
				};
				events.value.push(inEvent);

				const outEvent = {
					start: oneKidList.value[i].attendanceDate,
					end: oneKidList.value[i].attendanceDate,
					content: `하원 ${oneKidList.value[i].attendanceOutTime}`,
					class: 'out-event',
					allDay: true,
				};
				events.value.push(outEvent);
			}
		}
	}
};

onMounted(async () => {
	await getList();
});
</script>

<style scoped>
:deep(.vuecal__title-bar) {
	@apply h-16 bg-white font-bold text-xl;
}
:deep(.vuecal__heading) {
	@apply bg-nav-purple bg-opacity-30  font-bold text-base;
}
/* red & blue version  */
/* :deep(.vuecal__event.in-event) {
	@apply border-x border-t border-white rounded-t bg-nav-blue bg-opacity-50 font-bold text-gray-900 pt-2;
}
:deep(.vuecal__event.out-event) {
	@apply border-x border-b border-white rounded-b bg-nav-blue bg-opacity-50 font-bold text-gray-900 pb-2;
}
:deep(.vuecal__event.false-event) {
	@apply border-x border-b border-white rounded-b bg-nav-blue bg-opacity-50 font-bold text-gray-500 pb-2;
}
:deep(.vuecal__event.no-event) {
	@apply rounded shadow-md bg-nav-red bg-opacity-50 font-bold text-gray-900 py-5;
} */

/* white & black version */
:deep(.vuecal__event.in-event) {
	@apply rounded-t text-lg font-bold  text-gray-900 pt-2;
}
:deep(.vuecal__event.out-event) {
	@apply rounded-b text-lg font-bold shadow-md text-gray-900 pt-0.5 pb-2.5;
}
:deep(.vuecal__event.false-event) {
	@apply rounded-b text-lg font-medium shadow-md text-gray-400 pt-0.5 pb-2.5;
}
:deep(.vuecal__event.no-event) {
	@apply rounded-md text-lg font-bold shadow-md text-red-600 py-6;
}
:deep(.vuecal__cell) {
	@apply h-32;
}
:deep(.vuecal__cell--selected) {
	@apply bg-nav-purple bg-opacity-10;
}
:deep(.vuecal__cell-date) {
	@apply mb-1;
}
:deep(.vuecal__cell--today) {
	@apply bg-nav-purple bg-opacity-50;
}
</style>
