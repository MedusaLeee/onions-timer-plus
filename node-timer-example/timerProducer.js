/**
 * 同一时间点并发1000 延迟测试 15秒，和 quartz线程数有关
 */
const AMQP = require('amqplib')
const moment = require('moment')
const startAt = moment().unix() * 1000 + 1 * 60 * 1000
const startProducer = async (conn, messageCount) => {
    const channel = await conn.createConfirmChannel()
    const producerQueue = 'timerProducerQ'
    // 测试在同一时间时间点 触发多个任务的延迟, 当前时间 + 2 分钟
    // 定义队列
    await channel.assertQueue(producerQueue, { durable: true })
    for (let i = 0; i < messageCount; i += 1) {
        const message = {
            startAt,
            data: {
                startAt: startAt,
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
            const timeDiff = moment().unix() * 1000 - startAt
            console.log('consumer timeDiff: ', timeDiff, ',message: ', message)
        } catch (e) {
            console.error('consumer err: ' + e.toString())
        } finally {
            channel.ack(msg)
            console.log('consumer message success ...')
        }
    }, { noAck: false })
    console.log('start consumer success....')
}

const main = async (messageCount, prefetch) => {
    const conn = await AMQP.connect('amqp://localhost')
    startConsumer(conn, prefetch).then()
    startProducer(conn, messageCount).then()
}

main(500, 50).then()
