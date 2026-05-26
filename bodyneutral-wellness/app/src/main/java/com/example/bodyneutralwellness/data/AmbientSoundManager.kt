package com.example.bodyneutralwellness.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.math.sin

class AmbientSoundManager(private val context: Context) {

    private var audioTrack: AudioTrack? = null
    private val isPlaying = AtomicBoolean(false)
    private var activeThread: Thread? = null
    private var activeSound = "None"

    fun startSound(soundName: String) {
        if (soundName == "None") {
            stopSound()
            return
        }

        stopSound()
        isPlaying.set(true)
        activeSound = soundName

        activeThread = thread(start = true) {
            runSynthLoop(soundName)
        }
    }

    fun stopSound() {
        isPlaying.set(false)
        activeThread?.interrupt()
        activeThread = null
        try {
            audioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
        activeSound = "None"
    }

    fun getActiveSound(): String = activeSound

    private fun runSynthLoop(soundName: String) {
        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack = track
        track.play()

        val buffer = ShortArray(bufferSize / 2)
        var phase = 0.0
        var noiseLastOut = 0.0f
        var waveModTime = 0.0

        while (isPlaying.get() && !Thread.currentThread().isInterrupted) {
            for (i in buffer.indices) {
                var sample = 0.0f

                when (soundName) {
                    "Gentle Rain" -> {
                        // Blend of high frequency crackle (rain drop hits) and brown noise
                        val white = (Math.random() * 2.0 - 1.0).toFloat()
                        // Brownian noise filter
                        noiseLastOut = (noiseLastOut + (0.05f * white)) / 1.05f
                        
                        // Sparse raindrops crackle
                        val crackle = if (Math.random() < 0.002) {
                            (Math.random() * 2.0 - 1.0).toFloat() * 0.4f
                        } else 0.0f

                        sample = (noiseLastOut * 0.6f + crackle) * 0.25f
                    }
                    "Ocean Waves" -> {
                        // LFO (Low Frequency Oscillator) to modulate noise volume (simulating rolling waves)
                        val white = (Math.random() * 2.0 - 1.0).toFloat()
                        noiseLastOut = (noiseLastOut + (0.02f * white)) / 1.02f

                        waveModTime += 1.0 / sampleRate
                        val waveLfo = (sin(2.0 * Math.PI * 0.08 * waveModTime) + 1.0) / 2.0 // 0.08 Hz (12s waves cycle)
                        val gentleWindLfo = (sin(2.0 * Math.PI * 0.25 * waveModTime) + 1.0) / 2.0 * 0.15

                        sample = (noiseLastOut * (waveLfo + gentleWindLfo).toFloat()) * 0.2f
                    }
                    "Cozy Hearth" -> {
                        // Crackles (fire pop) + low rumble rumble (brown noise)
                        val white = (Math.random() * 2.0 - 1.0).toFloat()
                        noiseLastOut = (noiseLastOut + (0.015f * white)) / 1.015f

                        val pop = if (Math.random() < 0.0008) {
                            (Math.random() * 2.0 - 1.0).toFloat() * 0.6f
                        } else 0.0f

                        sample = (noiseLastOut * 0.7f + pop * 0.3f) * 0.2f
                    }
                    else -> {
                        // Soft soothing 432Hz sine wave oscillator
                        val frequency = 432.0
                        phase += 2.0 * Math.PI * frequency / sampleRate
                        if (phase > 2.0 * Math.PI) phase -= 2.0 * Math.PI
                        sample = sin(phase).toFloat() * 0.15f
                    }
                }

                // Clip control
                if (sample > 1.0f) sample = 1.0f
                if (sample < -1.0f) sample = -1.0f

                buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
        }

        try {
            track.stop()
            track.release()
        } catch (e: Exception) {
            // ignore cleanup details if interrupted
        }
    }
}
