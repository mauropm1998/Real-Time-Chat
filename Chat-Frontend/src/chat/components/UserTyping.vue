<template>
    <div :class="[
        'flex flex-row items-start gap-2 mb-4 -mt-3.5'
    ]">
        <div class="flex-shrink-0">
            <div class="w-8 h-8 bg-transparent"></div>
        </div>

        <div :class="[
            'max-w-[80%] sm:max-w-[60%] md:max-w-[65%] relative group items-start'
        ]">
            <div :class="[
                'rounded-2xl px-3 py-3 relative bg-white dark:bg-gray-800 text-gray-900 dark:text-white border border-gray-200 dark:border-gray-700',
            ]">
                <div class="text-[.92rem]">
                    <div class="flex gap-1">
                        <span class="block w-2 h-2 bg-gray-400 dark:bg-white/80 rounded-full animate-bounce"
                            style="animation-delay:0s"></span>
                        <span class="block w-2 h-2 bg-gray-400 dark:bg-white/80 rounded-full animate-bounce"
                            style="animation-delay:0.15s"></span>
                        <span class="block w-2 h-2 bg-gray-400 dark:bg-white/80 rounded-full animate-bounce"
                            style="animation-delay:0.3s"></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>

</script>