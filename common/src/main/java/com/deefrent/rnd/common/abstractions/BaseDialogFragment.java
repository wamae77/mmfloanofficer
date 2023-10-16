package com.deefrent.rnd.common.abstractions;

import android.os.Bundle;

import androidx.annotation.Nullable;

import dagger.android.support.DaggerDialogFragment;

public class BaseDialogFragment extends DaggerDialogFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


   /* private fun initDpSDK() {
        try {
            Globals.ClearLastBitmap()
            // initialize dp sdk
            val applContext: Context = requireActivity().getApplicationContext()

            // try to get the reader as long as its still within 5 seconds
            val startTime = System.currentTimeMillis()
            readers = Globals.getInstance().getReaders(applContext)
            while (readers.size == 0 && System.currentTimeMillis() - startTime < 5000) {
                readers = Globals.getInstance().getReaders(applContext)
            }

//            Toast.makeText(this, String.valueOf(readers.size()), Toast.LENGTH_SHORT).show();
            if (requireActivity().getIntent() != null && requireActivity().getIntent()
                    .getAction() != null && requireActivity().getIntent()
                    .getAction() == UsbManager.ACTION_USB_DEVICE_ATTACHED
            ) {
                InitDevice(0)
            }
            if (readers.size > 0) {
                m_deviceName = readers[0].GetDescription().name
                setUpDevice()
                val i = Intent()
                i.putExtra("device_name", m_deviceName)
                requireActivity().setResult(Activity.RESULT_OK, i)
                // finish()
            } else {
                displayReaderNotFound("Else")
            }
        } catch (e: UareUException) {
            displayReaderNotFound(e.message.toString())
        }
    }

    private fun InitDevice(position: Int) {
        try {
            readers[position].Open(Reader.Priority.COOPERATIVE)
            readers[position].Close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun setUpDevice() {
        if (m_deviceName != null && !m_deviceName.isEmpty()) {
            try {
                m_reader = Globals.getInstance()
                        .getReader(m_deviceName, requireActivity().applicationContext)
                if (DPFPDDUsbHost.DPFPDDUsbCheckAndRequestPermissions(
                        requireActivity().applicationContext,
                        mPermissionIntent,
                        m_deviceName
                )
                ) {
                    CheckDevice()
                }
            } catch (e1: UareUException) {
                displayReaderNotFound()
            } catch (e: DPFPDDUsbException) {
                displayReaderNotFound(e.message.toString())
            }
        } else {
            displayReaderNotFound("setUpDevice")
        }
    }*/
}
