<template>
  <section class="container">
    <div id="shopping-list">
      <div class="header">
        <h1>{{ header || "Welcome" }}</h1>
        <button v-if="editing" @click="doEdit(false)" class="btn btn-cancel">
          Cancel
        </button>
        <button v-else @click="doEdit(true)" class="btn btn-primary">
          Add Note
        </button>
      </div>

      <div v-if="editing" class="add-item-form">
        <input
          @keyup.enter="saveItem"
          type="text"
          v-model="newItem"
          placeholder="Add a note"
        />
        <!--<label>
          <input
            type="checkbox"
            v-model="newItemHighPriority"
            style="border: none; border-radius: none; box-shadow: none"
          />
          High Priority
        </label>-->
        <button
          :disabled="newItem.length === 0"
          @click="saveItem"
          class="btn btn-primary"
        >
          Save
        </button>
      </div>
      <p v-if="items.length === 0">No notes here!</p>
      <ul>
        <li
          v-for="item in reversedItems"
          @click="togglePurchased(item)"
          :key="item.id"
          :class="{ strikeout: item.purchased, priority: item.highPriority }"
          class="static-class"
        >
          {{ item.label }}
        </li>
      </ul>
    </div>
  </section>
</template>
<script>
export default {
  data: () => ({
    header: "Notes",
    editing: false,
    newItem: "",
    newItemHighPriority: false,
    items: [],
  }),
  computed: {
    reversedItems() {
      return [...this.items].reverse();
    },
  },
  methods: {
    saveItem() {
      this.items.push({
        id: this.items.length + 1,
        label: this.newItem,
        highPriority: this.newItemHighPriority,
      });
      this.newItem = "";
      this.newItemHighPriority = "";
    },
    doEdit(editing) {
      this.editing = editing;
      this.newItem = "";
      this.newItemHighPriority = "";
    },
    togglePurchased(item) {
      item.purchased = !item.purchased;
    },
  },
};
</script>
