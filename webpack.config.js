var path = require('path');

module.exports = {
    entry: './src/main/js/index.js',
    cache: true,
    mode: "development",
    output: {
        path: __dirname,
        filename: './src/main/resources/static/built/bundle.js'
    },
    module: {
        rules: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: ["@babel/preset-env", "@babel/preset-react"]
                    }
                }]
            },
            {
                test: /\.css$/,
                use: [
                    'style-loader',
                    'css-loader',
                    {
                        loader: "postcss-loader",
                        options: {
                            postcssOptions: {
                                plugins: [
                                    ['postcss-preset-mantine', {}],
                                    ['postcss-simple-vars', {
                                        variables: {
                                            'mantine-breakpoint-xs': '36em',
                                            'mantine-breakpoint-sm': '48em',
                                            'mantine-breakpoint-md': '62em',
                                            'mantine-breakpoint-lg': '75em',
                                            'mantine-breakpoint-xl': '88em',
                                        },
                                    }]
                                ],
                            },
                        },
                    }
                ],
            }
        ]
    }
};
