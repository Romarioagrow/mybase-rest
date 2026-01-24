const merge = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common, {
    mode: 'development',
    devtool: 'source-map',
    devServer: {
        //contentBase: './dist',
        //disableHostCheck: true,
        compress: true,
        port: 8000,
        allowedHosts: [
            'localhost',
            '127.0.0.1'
        ],
        proxy: {
            '/api': {
                target: 'http://localhost:9000',
                changeOrigin: true
            }
        }
    },
});
