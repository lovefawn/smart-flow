// vite.config.js
import { resolve } from "path";
import vue from "file:///D:/dev/flow/smart-flow/smart-flow-web/node_modules/@vitejs/plugin-vue/dist/index.mjs";

// src/theme/custom-variables.js
import { theme } from "file:///D:/dev/flow/smart-flow/smart-flow-web/node_modules/ant-design-vue/lib/index.js";
import convertLegacyToken from "file:///D:/dev/flow/smart-flow/smart-flow-web/node_modules/ant-design-vue/lib/theme/convertLegacyToken.js";
var { defaultAlgorithm, defaultSeed } = theme;
var mapToken = defaultAlgorithm(defaultSeed);
var token = convertLegacyToken.default(mapToken);
var custom_variables_default = {
  "@primary-color": token["primary-color"],
  // 全局主色
  "@base-bg-color": "#fff",
  "@hover-bg-color": "rgba(0, 0, 0, 0.025)",
  "@hover-bg-color-night": "rgba(255, 255, 255, 0.025)",
  "@header-light-bg-hover-color": "#f6f6f6",
  "@header-height": "80px",
  "@header-user-height": "40px",
  "@page-tag-height": "40px",
  "@theme-list": ["light", "dark", "night"]
};

// vite.config.js
var __vite_injected_original_dirname = "D:\\dev\\flow\\smart-flow\\smart-flow-web";
var pathResolve = (dir) => {
  return resolve(__vite_injected_original_dirname, ".", dir);
};
var vite_config_default = {
  base: process.env.NODE_ENV === "production" ? "/" : "/",
  root: process.cwd(),
  resolve: {
    alias: [
      // 国际化替换
      {
        find: "vue-i18n",
        replacement: "vue-i18n/dist/vue-i18n.cjs.js"
      },
      // 绝对路径重命名：/@/xxxx => src/xxxx
      {
        find: /\/@\//,
        replacement: pathResolve("src") + "/"
      },
      {
        find: /^~/,
        replacement: ""
      }
    ]
  },
  // 服务端渲染
  server: {
    host: "0.0.0.0",
    port: 8181
  },
  plugins: [vue()],
  optimizeDeps: {
    include: ["ant-design-vue/es/locale/zh_CN", "dayjs/locale/zh-cn", "ant-design-vue/es/locale/en_US"],
    exclude: ["vue-demi"]
  },
  build: {
    // 清除console和debugger
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    },
    rollupOptions: {
      output: {
        //配置这个是让不同类型文件放在不同文件夹，不会显得太乱
        chunkFileNames: "js/[name]-[hash].js",
        entryFileNames: "js/[name]-[hash].js",
        assetFileNames: "[ext]/[name]-[hash].[ext]",
        manualChunks(id) {
          if (id.includes("node_modules")) {
            return id.toString().split("node_modules/")[1].split("/")[0].toString();
          }
        }
      }
    },
    target: "modules",
    outDir: "dist",
    // 指定输出路径
    assetsDir: "assets",
    // 指定生成静态文件目录
    assetsInlineLimit: "4096",
    // 小于此阈值的导入或引用资源将内联为 base64 编码
    chunkSizeWarningLimit: 500,
    // chunk 大小警告的限制
    minify: "terser",
    // 混淆器，terser构建后文件体积更小
    emptyOutDir: true
    //打包前先清空原有打包文件
  },
  css: {
    preprocessorOptions: {
      less: {
        modifyVars: custom_variables_default,
        javascriptEnabled: true
      }
    }
  },
  define: {
    __INTLIFY_PROD_DEVTOOLS__: false,
    "process.env": process.env
  }
};
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcuanMiLCAic3JjL3RoZW1lL2N1c3RvbS12YXJpYWJsZXMuanMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJEOlxcXFxkZXZcXFxcZmxvd1xcXFxzbWFydC1mbG93XFxcXHNtYXJ0LWZsb3ctd2ViXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ZpbGVuYW1lID0gXCJEOlxcXFxkZXZcXFxcZmxvd1xcXFxzbWFydC1mbG93XFxcXHNtYXJ0LWZsb3ctd2ViXFxcXHZpdGUuY29uZmlnLmpzXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ltcG9ydF9tZXRhX3VybCA9IFwiZmlsZTovLy9EOi9kZXYvZmxvdy9zbWFydC1mbG93L3NtYXJ0LWZsb3ctd2ViL3ZpdGUuY29uZmlnLmpzXCI7LypcclxuICogdml0ZVx1OTE0RFx1N0Y2RVxyXG4gKlxyXG4gKiBAQXV0aG9yOiAgICAxMDI0XHU1MjFCXHU2NUIwXHU1QjlFXHU5QThDXHU1QkE0LVx1NEUzQlx1NEVGQlx1RkYxQVx1NTM1M1x1NTkyN1xyXG4gKiBARGF0ZTogICAgICAyMDIyLTA1LTAyIDIzOjQ0OjU2XHJcbiAqIEBXZWNoYXQ6ICAgIHpodWRhMTAyNFxyXG4gKiBARW1haWw6ICAgICBsYWIxMDI0QDE2My5jb21cclxuICogQENvcHlyaWdodCAgMTAyNFx1NTIxQlx1NjVCMFx1NUI5RVx1OUE4Q1x1NUJBNCBcdUZGMDggaHR0cHM6Ly8xMDI0bGFiLm5ldCBcdUZGMDlcdUZGMENTaW5jZSAyMDEyXHJcbiAqL1xyXG5pbXBvcnQgeyByZXNvbHZlIH0gZnJvbSAncGF0aCc7XHJcbmltcG9ydCB2dWUgZnJvbSAnQHZpdGVqcy9wbHVnaW4tdnVlJztcclxuaW1wb3J0IGN1c3RvbVZhcmlhYmxlcyBmcm9tICcvQC90aGVtZS9jdXN0b20tdmFyaWFibGVzLmpzJztcclxuXHJcbmNvbnN0IHBhdGhSZXNvbHZlID0gKGRpcikgPT4ge1xyXG4gIHJldHVybiByZXNvbHZlKF9fZGlybmFtZSwgJy4nLCBkaXIpO1xyXG59O1xyXG5leHBvcnQgZGVmYXVsdCB7XHJcbiAgYmFzZTogcHJvY2Vzcy5lbnYuTk9ERV9FTlYgPT09ICdwcm9kdWN0aW9uJyA/ICcvJyA6ICcvJyxcclxuICByb290OiBwcm9jZXNzLmN3ZCgpLFxyXG4gIHJlc29sdmU6IHtcclxuICAgIGFsaWFzOiBbXHJcbiAgICAgIC8vIFx1NTZGRFx1OTY0NVx1NTMxNlx1NjZGRlx1NjM2MlxyXG4gICAgICB7XHJcbiAgICAgICAgZmluZDogJ3Z1ZS1pMThuJyxcclxuICAgICAgICByZXBsYWNlbWVudDogJ3Z1ZS1pMThuL2Rpc3QvdnVlLWkxOG4uY2pzLmpzJyxcclxuICAgICAgfSxcclxuICAgICAgLy8gXHU3RUREXHU1QkY5XHU4REVGXHU1Rjg0XHU5MUNEXHU1NDdEXHU1NDBEXHVGRjFBL0AveHh4eCA9PiBzcmMveHh4eFxyXG4gICAgICB7XHJcbiAgICAgICAgZmluZDogL1xcL0BcXC8vLFxyXG4gICAgICAgIHJlcGxhY2VtZW50OiBwYXRoUmVzb2x2ZSgnc3JjJykgKyAnLycsXHJcbiAgICAgIH0sXHJcbiAgICAgIHtcclxuICAgICAgICBmaW5kOiAvXn4vLFxyXG4gICAgICAgIHJlcGxhY2VtZW50OiAnJyxcclxuICAgICAgfSxcclxuICAgIF0sXHJcbiAgfSxcclxuICAvLyBcdTY3MERcdTUyQTFcdTdBRUZcdTZFMzJcdTY3RDNcclxuICBzZXJ2ZXI6IHtcclxuICAgIGhvc3Q6ICcwLjAuMC4wJyxcclxuICAgIHBvcnQ6IDgxODEsXHJcbiAgfSxcclxuICBwbHVnaW5zOiBbdnVlKCldLFxyXG4gIG9wdGltaXplRGVwczoge1xyXG4gICAgaW5jbHVkZTogWydhbnQtZGVzaWduLXZ1ZS9lcy9sb2NhbGUvemhfQ04nLCAnZGF5anMvbG9jYWxlL3poLWNuJywgJ2FudC1kZXNpZ24tdnVlL2VzL2xvY2FsZS9lbl9VUyddLFxyXG4gICAgZXhjbHVkZTogWyd2dWUtZGVtaSddLFxyXG4gIH0sXHJcbiAgYnVpbGQ6IHtcclxuICAgIC8vIFx1NkUwNVx1OTY2NGNvbnNvbGVcdTU0OENkZWJ1Z2dlclxyXG4gICAgdGVyc2VyT3B0aW9uczoge1xyXG4gICAgICBjb21wcmVzczoge1xyXG4gICAgICAgIGRyb3BfY29uc29sZTogdHJ1ZSxcclxuICAgICAgICBkcm9wX2RlYnVnZ2VyOiB0cnVlLFxyXG4gICAgICB9LFxyXG4gICAgfSxcclxuICAgIHJvbGx1cE9wdGlvbnM6IHtcclxuICAgICAgb3V0cHV0OiB7XHJcbiAgICAgICAgLy9cdTkxNERcdTdGNkVcdThGRDlcdTRFMkFcdTY2MkZcdThCQTlcdTRFMERcdTU0MENcdTdDN0JcdTU3OEJcdTY1ODdcdTRFRjZcdTY1M0VcdTU3MjhcdTRFMERcdTU0MENcdTY1ODdcdTRFRjZcdTU5MzlcdUZGMENcdTRFMERcdTRGMUFcdTY2M0VcdTVGOTdcdTU5MkFcdTRFNzFcclxuICAgICAgICBjaHVua0ZpbGVOYW1lczogJ2pzL1tuYW1lXS1baGFzaF0uanMnLFxyXG4gICAgICAgIGVudHJ5RmlsZU5hbWVzOiAnanMvW25hbWVdLVtoYXNoXS5qcycsXHJcbiAgICAgICAgYXNzZXRGaWxlTmFtZXM6ICdbZXh0XS9bbmFtZV0tW2hhc2hdLltleHRdJyxcclxuICAgICAgICBtYW51YWxDaHVua3MoaWQpIHtcclxuICAgICAgICAgIC8vXHU5NzU5XHU2MDAxXHU4RDQ0XHU2RTkwXHU1MjA2XHU2MkM2XHU2MjUzXHU1MzA1XHJcbiAgICAgICAgICBpZiAoaWQuaW5jbHVkZXMoJ25vZGVfbW9kdWxlcycpKSB7XHJcbiAgICAgICAgICAgIHJldHVybiBpZC50b1N0cmluZygpLnNwbGl0KCdub2RlX21vZHVsZXMvJylbMV0uc3BsaXQoJy8nKVswXS50b1N0cmluZygpO1xyXG4gICAgICAgICAgfVxyXG4gICAgICAgIH0sXHJcbiAgICAgIH0sXHJcbiAgICB9LFxyXG4gICAgdGFyZ2V0OiAnbW9kdWxlcycsXHJcbiAgICBvdXREaXI6ICdkaXN0JywgLy8gXHU2MzA3XHU1QjlBXHU4RjkzXHU1MUZBXHU4REVGXHU1Rjg0XHJcbiAgICBhc3NldHNEaXI6ICdhc3NldHMnLCAvLyBcdTYzMDdcdTVCOUFcdTc1MUZcdTYyMTBcdTk3NTlcdTYwMDFcdTY1ODdcdTRFRjZcdTc2RUVcdTVGNTVcclxuICAgIGFzc2V0c0lubGluZUxpbWl0OiAnNDA5NicsIC8vIFx1NUMwRlx1NEU4RVx1NkI2NFx1OTYwOFx1NTAzQ1x1NzY4NFx1NUJGQ1x1NTE2NVx1NjIxNlx1NUYxNVx1NzUyOFx1OEQ0NFx1NkU5MFx1NUMwNlx1NTE4NVx1ODA1NFx1NEUzQSBiYXNlNjQgXHU3RjE2XHU3ODAxXHJcbiAgICBjaHVua1NpemVXYXJuaW5nTGltaXQ6IDUwMCwgLy8gY2h1bmsgXHU1OTI3XHU1QzBGXHU4QjY2XHU1NDRBXHU3Njg0XHU5NjUwXHU1MjM2XHJcbiAgICBtaW5pZnk6ICd0ZXJzZXInLCAvLyBcdTZERjdcdTZEQzZcdTU2NjhcdUZGMEN0ZXJzZXJcdTY3ODRcdTVFRkFcdTU0MEVcdTY1ODdcdTRFRjZcdTRGNTNcdTc5RUZcdTY2RjRcdTVDMEZcclxuICAgIGVtcHR5T3V0RGlyOiB0cnVlLCAvL1x1NjI1M1x1NTMwNVx1NTI0RFx1NTE0OFx1NkUwNVx1N0E3QVx1NTM5Rlx1NjcwOVx1NjI1M1x1NTMwNVx1NjU4N1x1NEVGNlxyXG4gIH0sXHJcbiAgY3NzOiB7XHJcbiAgICBwcmVwcm9jZXNzb3JPcHRpb25zOiB7XHJcbiAgICAgIGxlc3M6IHtcclxuICAgICAgICBtb2RpZnlWYXJzOiBjdXN0b21WYXJpYWJsZXMsXHJcbiAgICAgICAgamF2YXNjcmlwdEVuYWJsZWQ6IHRydWUsXHJcbiAgICAgIH0sXHJcbiAgICB9LFxyXG4gIH0sXHJcbiAgZGVmaW5lOiB7XHJcbiAgICBfX0lOVExJRllfUFJPRF9ERVZUT09MU19fOiBmYWxzZSxcclxuICAgICdwcm9jZXNzLmVudic6IHByb2Nlc3MuZW52LFxyXG4gIH0sXHJcbn07XHJcbiIsICJjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZGlybmFtZSA9IFwiRDpcXFxcZGV2XFxcXGZsb3dcXFxcc21hcnQtZmxvd1xcXFxzbWFydC1mbG93LXdlYlxcXFxzcmNcXFxcdGhlbWVcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZmlsZW5hbWUgPSBcIkQ6XFxcXGRldlxcXFxmbG93XFxcXHNtYXJ0LWZsb3dcXFxcc21hcnQtZmxvdy13ZWJcXFxcc3JjXFxcXHRoZW1lXFxcXGN1c3RvbS12YXJpYWJsZXMuanNcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfaW1wb3J0X21ldGFfdXJsID0gXCJmaWxlOi8vL0Q6L2Rldi9mbG93L3NtYXJ0LWZsb3cvc21hcnQtZmxvdy13ZWIvc3JjL3RoZW1lL2N1c3RvbS12YXJpYWJsZXMuanNcIjtpbXBvcnQgeyB0aGVtZSB9IGZyb20gJ2FudC1kZXNpZ24tdnVlL2xpYic7XHJcbmltcG9ydCBjb252ZXJ0TGVnYWN5VG9rZW4gZnJvbSAnYW50LWRlc2lnbi12dWUvbGliL3RoZW1lL2NvbnZlcnRMZWdhY3lUb2tlbic7XHJcblxyXG5jb25zdCB7IGRlZmF1bHRBbGdvcml0aG0sIGRlZmF1bHRTZWVkIH0gPSB0aGVtZTtcclxuXHJcbmNvbnN0IG1hcFRva2VuID0gZGVmYXVsdEFsZ29yaXRobShkZWZhdWx0U2VlZCk7XHJcbmNvbnN0IHRva2VuID0gY29udmVydExlZ2FjeVRva2VuLmRlZmF1bHQobWFwVG9rZW4pO1xyXG5cclxuZXhwb3J0IGRlZmF1bHQge1xyXG4gICdAcHJpbWFyeS1jb2xvcic6IHRva2VuWydwcmltYXJ5LWNvbG9yJ10sIC8vIFx1NTE2OFx1NUM0MFx1NEUzQlx1ODI3MlxyXG4gICdAYmFzZS1iZy1jb2xvcic6ICcjZmZmJyxcclxuICAnQGhvdmVyLWJnLWNvbG9yJzogJ3JnYmEoMCwgMCwgMCwgMC4wMjUpJyxcclxuICAnQGhvdmVyLWJnLWNvbG9yLW5pZ2h0JzogJ3JnYmEoMjU1LCAyNTUsIDI1NSwgMC4wMjUpJyxcclxuICAnQGhlYWRlci1saWdodC1iZy1ob3Zlci1jb2xvcic6ICcjZjZmNmY2JyxcclxuICAnQGhlYWRlci1oZWlnaHQnOiAnODBweCcsXHJcbiAgJ0BoZWFkZXItdXNlci1oZWlnaHQnOiAnNDBweCcsXHJcbiAgJ0BwYWdlLXRhZy1oZWlnaHQnOiAnNDBweCcsXHJcbiAgJ0B0aGVtZS1saXN0JzogWydsaWdodCcsICdkYXJrJywgJ25pZ2h0J10sXHJcbn07XHJcbiJdLAogICJtYXBwaW5ncyI6ICI7QUFTQSxTQUFTLGVBQWU7QUFDeEIsT0FBTyxTQUFTOzs7QUNWdVUsU0FBUyxhQUFhO0FBQzdXLE9BQU8sd0JBQXdCO0FBRS9CLElBQU0sRUFBRSxrQkFBa0IsWUFBWSxJQUFJO0FBRTFDLElBQU0sV0FBVyxpQkFBaUIsV0FBVztBQUM3QyxJQUFNLFFBQVEsbUJBQW1CLFFBQVEsUUFBUTtBQUVqRCxJQUFPLDJCQUFRO0FBQUEsRUFDYixrQkFBa0IsTUFBTSxlQUFlO0FBQUE7QUFBQSxFQUN2QyxrQkFBa0I7QUFBQSxFQUNsQixtQkFBbUI7QUFBQSxFQUNuQix5QkFBeUI7QUFBQSxFQUN6QixnQ0FBZ0M7QUFBQSxFQUNoQyxrQkFBa0I7QUFBQSxFQUNsQix1QkFBdUI7QUFBQSxFQUN2QixvQkFBb0I7QUFBQSxFQUNwQixlQUFlLENBQUMsU0FBUyxRQUFRLE9BQU87QUFDMUM7OztBRGxCQSxJQUFNLG1DQUFtQztBQWF6QyxJQUFNLGNBQWMsQ0FBQyxRQUFRO0FBQzNCLFNBQU8sUUFBUSxrQ0FBVyxLQUFLLEdBQUc7QUFDcEM7QUFDQSxJQUFPLHNCQUFRO0FBQUEsRUFDYixNQUFNLFFBQVEsSUFBSSxhQUFhLGVBQWUsTUFBTTtBQUFBLEVBQ3BELE1BQU0sUUFBUSxJQUFJO0FBQUEsRUFDbEIsU0FBUztBQUFBLElBQ1AsT0FBTztBQUFBO0FBQUEsTUFFTDtBQUFBLFFBQ0UsTUFBTTtBQUFBLFFBQ04sYUFBYTtBQUFBLE1BQ2Y7QUFBQTtBQUFBLE1BRUE7QUFBQSxRQUNFLE1BQU07QUFBQSxRQUNOLGFBQWEsWUFBWSxLQUFLLElBQUk7QUFBQSxNQUNwQztBQUFBLE1BQ0E7QUFBQSxRQUNFLE1BQU07QUFBQSxRQUNOLGFBQWE7QUFBQSxNQUNmO0FBQUEsSUFDRjtBQUFBLEVBQ0Y7QUFBQTtBQUFBLEVBRUEsUUFBUTtBQUFBLElBQ04sTUFBTTtBQUFBLElBQ04sTUFBTTtBQUFBLEVBQ1I7QUFBQSxFQUNBLFNBQVMsQ0FBQyxJQUFJLENBQUM7QUFBQSxFQUNmLGNBQWM7QUFBQSxJQUNaLFNBQVMsQ0FBQyxrQ0FBa0Msc0JBQXNCLGdDQUFnQztBQUFBLElBQ2xHLFNBQVMsQ0FBQyxVQUFVO0FBQUEsRUFDdEI7QUFBQSxFQUNBLE9BQU87QUFBQTtBQUFBLElBRUwsZUFBZTtBQUFBLE1BQ2IsVUFBVTtBQUFBLFFBQ1IsY0FBYztBQUFBLFFBQ2QsZUFBZTtBQUFBLE1BQ2pCO0FBQUEsSUFDRjtBQUFBLElBQ0EsZUFBZTtBQUFBLE1BQ2IsUUFBUTtBQUFBO0FBQUEsUUFFTixnQkFBZ0I7QUFBQSxRQUNoQixnQkFBZ0I7QUFBQSxRQUNoQixnQkFBZ0I7QUFBQSxRQUNoQixhQUFhLElBQUk7QUFFZixjQUFJLEdBQUcsU0FBUyxjQUFjLEdBQUc7QUFDL0IsbUJBQU8sR0FBRyxTQUFTLEVBQUUsTUFBTSxlQUFlLEVBQUUsQ0FBQyxFQUFFLE1BQU0sR0FBRyxFQUFFLENBQUMsRUFBRSxTQUFTO0FBQUEsVUFDeEU7QUFBQSxRQUNGO0FBQUEsTUFDRjtBQUFBLElBQ0Y7QUFBQSxJQUNBLFFBQVE7QUFBQSxJQUNSLFFBQVE7QUFBQTtBQUFBLElBQ1IsV0FBVztBQUFBO0FBQUEsSUFDWCxtQkFBbUI7QUFBQTtBQUFBLElBQ25CLHVCQUF1QjtBQUFBO0FBQUEsSUFDdkIsUUFBUTtBQUFBO0FBQUEsSUFDUixhQUFhO0FBQUE7QUFBQSxFQUNmO0FBQUEsRUFDQSxLQUFLO0FBQUEsSUFDSCxxQkFBcUI7QUFBQSxNQUNuQixNQUFNO0FBQUEsUUFDSixZQUFZO0FBQUEsUUFDWixtQkFBbUI7QUFBQSxNQUNyQjtBQUFBLElBQ0Y7QUFBQSxFQUNGO0FBQUEsRUFDQSxRQUFRO0FBQUEsSUFDTiwyQkFBMkI7QUFBQSxJQUMzQixlQUFlLFFBQVE7QUFBQSxFQUN6QjtBQUNGOyIsCiAgIm5hbWVzIjogW10KfQo=
