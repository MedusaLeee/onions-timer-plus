/**
 * 时间间隔 从长 4分钟 3 分钟 2 分钟 到 近，每组最多延迟4 秒，和 quartz线程数有关
 */
const AMQP = require('amqplib')
const moment = require('moment')
const startAt1 = moment().unix() * 1000 + 4 * 60 * 1000
const startAt2 = moment().unix() * 1000 + 3 * 60 * 1000
const startAt3 = moment().unix() * 1000 + 2 * 60 * 1000

const startProducer = async (conn, messageCount) => {
    const channel = await conn.createConfirmChannel()
    const producerQueue = 'timerProducerQ'
    // 测试在同一时间时间点 触发多个任务的延迟, 当前时间 + 2 分钟
    // 定义队列
    await channel.assertQueue(producerQueue, { durable: true })
    for (let i = 0; i < messageCount; i += 1) {
        const message = {
            startAt: startAt1,
            data: {
                startAt: startAt1,
                data: "你好"
            }
        }
        await channel.sendToQueue(producerQueue, Buffer.from(JSON.stringify(message)), {
            mandatory: true,
            headers: { retrySum: 1 } // TODO 预留，重试时使用
        }, (err) => {
            if (err) {
                return console.error(`sendToQueue ${producerQueue} error: ${err.toString()}`)
            }
            console.log(`sendToQueue ${producerQueue} success: ${JSON.stringify(message)}`)
        })
        console.log(`total: ${messageCount}, send message progress ${i} ...`)
    }
    for (let i = 0; i < messageCount; i += 1) {
        const message = {
            startAt: startAt2,
            data: {
                startAt: startAt2,
                data: "你好"
            }
        }
        await channel.sendToQueue(producerQueue, Buffer.from(JSON.stringify(message)), {
            mandatory: true,
            headers: { retrySum: 1 } // TODO 预留，重试时使用
        }, (err) => {
            if (err) {
                return console.error(`sendToQueue ${producerQueue} error: ${err.toString()}`)
            }
            console.log(`sendToQueue ${producerQueue} success: ${JSON.stringify(message)}`)
        })
        console.log(`total: ${messageCount}, send message progress ${i} ...`)
    }
    for (let i = 0; i < messageCount; i += 1) {
        const message = {
            startAt: startAt3,
            data: {
                startAt: startAt3,
                data: "你好"
            }
        }
        await channel.sendToQueue(producerQueue, Buffer.from(JSON.stringify(message)), {
            mandatory: true,
            headers: { retrySum: 1 } // TODO 预留，重试时使用
        }, (err) => {
            if (err) {
                return console.error(`sendToQueue ${producerQueue} error: ${err.toString()}`)
            }
            console.log(`sendToQueue ${producerQueue} success: ${JSON.stringify(message)}`)
        })
        console.log(`total: ${messageCount}, send message progress ${i} ...`)
    }
}

const startConsumer = async (conn, prefetch) => {
    const channel = await conn.createConfirmChannel()
    const consumerQueue = 'timerConsumerQ'
    // 定义队列
    await channel.assertQueue(consumerQueue, { durable: true })
    // 预加载1个消息
    await channel.prefetch(parseInt(prefetch))
    // 监听并消费通知队列
    await channel.consume(consumerQueue, async (msg) => {
        try {
            const message = JSON.parse(msg.content.toString())
            const timeDiff = moment().unix() * 1000 - message.startAt
            // console.log('consumer timeDiff: ', timeDiff, ',message: ', message)
            console.log('consumer timeDiff: ', timeDiff, moment(message.startAt).format('HH:mm:ss'))
        } catch (e) {
            console.error('consumer err: ' + e.toString())
        } finally {
            channel.ack(msg)
            // console.log('consumer message success ...')
        }
    }, { noAck: false })
    console.log('start consumer success....')
}

const main = async (messageCount, prefetch) => {
    const conn = await AMQP.connect('amqp://localhost')
    startConsumer(conn, prefetch).then()
    startProducer(conn, messageCount).then()
}

main(300, 50).then()
