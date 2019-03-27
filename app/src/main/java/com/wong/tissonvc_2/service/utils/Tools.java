package com.wong.tissonvc_2.service.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The type Tool.
 * <p>
 * Tool
 * Global config tool
 */
public final class Tools
{
    /**
     * The constant TAG.
     */
    private static final String TAG = Tools.class.getSimpleName();

    /**
     * Instantiates a new Tools.
     */
    private Tools()
    {
    }

    /**
     * isStringEmpty
     *
     * @param source the source
     * @return true /false
     */
    public static boolean isStringEmpty(String source)
    {
        return source == null || "".equals(source);
    }

    /**
     * isNotEmpty
     *
     * @param source the source
     * @return true /false
     */
    public static boolean isNotEmpty(String source)
    {

        return !isStringEmpty(source);
    }

    /**
     * String to int int.
     *
     * @param str the str
     * @return the int
     */
    public static int stringToInt(String str)
    {
        return stringToInt(str, -1);
    }

    /**
     * String to int int.
     *
     * @param str          the str
     * @param defaultValue the default value
     * @return the int
     */
    private static int stringToInt(String str, int defaultValue)
    {
        if (str == null)
        {
            return defaultValue;
        }

        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException var3)
        {
            return defaultValue;
        }

    }

    /**
     * Remove string.
     *
     * @param source the source
     * @param pos    the pos
     * @param c      the c
     * @return the string
     */
    public static String remove(String source, int pos, char c)
    {
        String result = source;
        if (source != null && !"".equals(source))
        {
            if (pos >= 0 && pos < source.length())
            {
                if (c == source.charAt(pos))
                {
                    result = source.substring(pos + 1);
                }

                return result;
            }
            else
            {
                return source;
            }
        }
        else
        {
            return "";
        }
    }

    /**
     * Is ip address boolean.
     *
     * @param iPAddress the p address
     * @return the boolean
     */
    public static boolean isIPAddress(String iPAddress)
    {
        Pattern p = null;
        if (iPAddress.contains(":"))
        {
            p = Pattern.compile("^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}"
                    + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9]):[0-9]{2,5}$");
        }
        else
        {
            p = Pattern.compile("^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}"
                    + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$");
        }

        return p.matcher(iPAddress).matches();
    }

    /**
     * Read bit map bitmap.
     *
     * @param context the context
     * @param resId   the res id
     * @return the bitmap
     */
    public static Bitmap readBitMap(Context context, int resId)
    {
        InputStream is = null;
        try
        {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            is = context.getResources().openRawResource(resId);
            return BitmapFactory.decodeStream(is, null, opt);
        }
        catch (Resources.NotFoundException e)
        {
            TUPLogUtil.e(TAG, "Progress get an NotFoundException");
        }
        finally
        {
            closeInputStream(is);

        }
        return null;
    }

    /**
     * Close input stream.
     *
     * @param is the is
     */
    private static void closeInputStream(InputStream is)
    {
        if (null == is)
        {
            return;
        }
        try
        {
            is.close();
        }
        catch (IOException e)
        {
            TUPLogUtil.e(TAG, "Progress get an IOException");
        }
    }

    /**
     * Gets vpn local ip.
     *
     * @return the vpn local ip
     */
    public static String getVpnLocalIp()
    {
        String ip = "";
        try
        {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces)
            {
                String displayName = networkInterface.getDisplayName();
                List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
                if (displayName.equals("ppp0") && addresses.size() > 0)
                {
                    ip = addresses.get(0).getAddress().getHostAddress();
                    Log.d(TAG, "ip = " + ip);
                    if (!TextUtils.isEmpty(ip))
                    {
                        break;
                    }
                }
            }
            return ip;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets ip address.
     *
     * @return the ip address
     */
    public static String getLocalIp()
    {
        String ip = "";
        try
        {
            Enumeration<NetworkInterface> networkInfo = NetworkInterface
                    .getNetworkInterfaces();
            NetworkInterface intf = null;
            Enumeration<InetAddress> intfAddress = null;
            InetAddress inetAddress = null;
            if (networkInfo == null)
            {
                TUPLogUtil.d("getLocalIp",
                        "get LocalIp address Error , return null value ");
                return "";
            }
            for (Enumeration<NetworkInterface> en = networkInfo; en
                    .hasMoreElements(); )
            {
                intf = en.nextElement();
                intfAddress = intf.getInetAddresses();
                for (Enumeration<InetAddress> enumIpAddr = intfAddress; enumIpAddr
                        .hasMoreElements(); )
                {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        ip = inetAddress.getHostAddress();
                        if (isIPV4Addr(ip))
                        {
                            TUPLogUtil.i("getLocalIp", "ip is " + ip);
                            return ip;
                        }
                    }
                }
            }
        }
        catch (SocketException e)
        {
            TUPLogUtil.d("getLocalIp", "SocketException | " + e.toString());
        }
        return ip;
    }

    /**
     * Is ipv 4 addr boolean.
     *
     * @param ipAddr the ip addr
     * @return the boolean
     */
    private static boolean isIPV4Addr(String ipAddr)
    {
        Pattern p = Pattern.compile("^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}"
                + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$");
        return p.matcher(ipAddr).matches();
    }

    /**
     * Is wifi connect boolean.
     *
     * @param context the context
     * @return the boolean
     */
    private static boolean isWIFIConnect(Context context)
    {
        if (context == null)
        {
            return false;
        }
        else
        {
            ConnectivityManager cm = (ConnectivityManager) context.
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = cm.getNetworkInfo(1);
            return wifi != null && wifi.isConnected();
        }
    }

    /**
     * Is 3 g connect boolean.
     *
     * @param context the context
     * @return the boolean
     */
    private static boolean is3GConnect(Context context)
    {
        if (context == null)
        {
            return false;
        }
        else
        {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            boolean supportType = false;
            NetworkInfo info = cm.getActiveNetworkInfo();
            TUPLogUtil.e(TAG, "info is : " + info);
            if (info != null)
            {
                TUPLogUtil.i(TAG, "info typr is : info.getSubtype() : "
                        + info.getSubtype() + "info.getSubtypeName() : "
                        + info.getSubtypeName() + "info.getType() : " + info.getType()
                        + "info.getTypeName() : " + info.getTypeName());
            }

            if (info == null)
            {
                TUPLogUtil.e(TAG, " MOBILE_TYPE is null  ! ");
                supportType = false;
            }
            else if (info.getType() == 0)
            {
                int subType = info.getSubtype();
                TUPLogUtil.e(TAG, "subType is : " + subType);
                if (subType != 4 && subType != 1 && subType != 2 && subType != 7 && subType != 11)
                {
                    if (is3g4gSubType(subType))
                    {
                        TUPLogUtil.i(TAG, "is 3g / 4g conect");
                        supportType = true;
                    }
                }
                else
                {
                    TUPLogUtil.i(TAG, "is 2g conect");
                    supportType = false;
                }
            }
            else if (info.getTypeName().equalsIgnoreCase("LTE") && info.isConnected())
            {
                supportType = true;
            }

            return supportType;
        }
    }

    /**
     * Is 3 g 4 g sub type boolean.
     *
     * @param subType the sub type
     * @return the boolean
     */
    private static boolean is3g4gSubType(int subType)
    {
        return (subType == 3 || subType == 8 || subType == 6 || subType == 5 || subType == 12
                || subType == 13 || subType == 14 || subType == 10 || subType == 9 || subType == 15);
    }


    /**
     * Is ethernet connect boolean.
     *
     * @param context the context
     * @return the boolean
     */
    private static boolean isEthernetConnect(Context context)
    {
        if (context == null)
        {
            return false;
        }
        else
        {
            ConnectivityManager cm = (ConnectivityManager) context.
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getNetworkInfo(9);
            if (info != null)
            {
                TUPLogUtil.i(TAG, info.toString());
                return info.isConnected();
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Is network available boolean.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null)
        {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length; ++i)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Is wifi or 3 g available boolean.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isWifiOr3GAvailable(Context context)
    {
        return isWIFIConnect(context) || is3GConnect(context) || isEthernetConnect(context);
    }


}
