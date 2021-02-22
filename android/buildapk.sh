#!/bin/bash

function print_usage {
  echo "Usage:"
  echo
  echo "  $0 [-options] -t TEMPLATE  -m MODEL_FILE [-options]"
  echo "    This script will build an Android application wrap for target website"
  echo ""
  echo "    -t                            TensorFlow Lite project template"
  echo "    -m                            TensorFlow Lite model file"
  echo "    -i                            install apk at once on connected device"
  echo "    -h:                           display this message"
  echo
}

function exit_and_print_usage {
	print_usage
	exit 1
}

function exit_with_error {
	exit 1
}

function squeezeAndLowerString() {
  orig_str=$*

  filter_str=${orig_str//-/\ }
  new_str=""
  for i in ${filter_str}; do
    tmp=`echo -n "${i:0:1}" | tr "[:upper:]" "[:lower:]"`;
    new_str=${new_str}"${tmp}${i:1}";
  done

  echo "${new_str}"
}

function modify_resources {
    sed_str="s/application_name_/${app_name}/g"
    files=`find . -name "strings.xml"`
    for file in ${files}; do
#        echo "processing file: ${file}"
        cat ${file} | perl -pe "${sed_str}" > ${file}.tmp
        mv ${file}.tmp ${file}
    done
}

function modify_build_scripts {
    sed_str="s/package_name_/${package_name//\//\\/}/g;"
    files=`find . -name "build.gradle"`
    for file in ${files}; do
#        echo "processing file: ${file}"
        cat ${file} | perl -pe ${sed_str} > ${file}.tmp
        mv ${file}.tmp ${file}
    done
}

app_name="TensorFlow Lite Viewer"
apk_file="app/build/outputs/apk/debug/app-debug.apk"
output_base_dir="./outputs/"
lite_model_dir="src/main/ml"
lite_model_fname="lite-model.tflite"

while getopts :t:m:n:ihH opt; do
  case ${opt} in
    t)
      template=${OPTARG}
      ;;
    m)
      model=${OPTARG}
      ;;
    n)
      app_name=${OPTARG}
    	;;
    i)
      install_at_once=true
      ;;
    h|H)
	    print_usage
	    exit 2
      ;;
    :)
	    echo "[ERROR] $0: -${OPTARG} requires an argument."
      exit_and_print_usage
      ;;
    *)
	    echo "[ERROR] $0: -${OPTARG} is unsuppported."
      exit_and_print_usage
      ;;
  esac
done

if [ -z "${template}" ] || [ -z "${model}" ]; then
    echo "[ERROR] required options is missing."
    exit_and_print_usage
fi

if [ ! -d "${template}" ]; then
    echo "[ERROR] project template [${template}] does NOT exist."
    exit_with_error
fi

if [ ! -f "${model}" ]; then
    echo "[ERROR] lite model file [${template}] does NOT exist."
    exit_with_error
fi

app_name="${app_name//\'/\\\\\'}"
app_name="${app_name%%. *}"

app_name_code=$(squeezeAndLowerString ${app_name})

output_fname=${app_name}.apk
output_file="${output_base_dir}/${template}/${output_fname}"
package_name=`sed 's/=/_/g' <<< "${app_name_code}"`
date=`date +%Y-%m-%dT%H:%M:%S`

echo "---------------------------------------------------------------------------------------------"
echo "Creating TensorFlow Lite model viewer: at ${date}"
echo "---------------------------------------------------------------------------------------------"
echo
echo "Configuration:"
echo "|- application name: ${app_name}"
echo "|- template:         ${template}"
echo "|- model:            ${model}"
echo "\`- package name:     ${package_name}"
echo
echo "---------------------------------------------------------------------------------------------"
echo

project_directory=${template}
echo "[1] clean up workspace ... ${project_directory}"
lite_model_dest="${project_directory}/${lite_model_dir}"
rm ${lite_model_dest}/*.tflite 2> /dev/null
git checkout ${template} 2> /dev/null

echo "[2] copy lite model into workspace ... ${lite_model_dest}"
cp ${model} ${lite_model_dest}/${lite_model_fname}

echo "[3] modifying code ..."
modify_resources
modify_build_scripts

echo "[4] compling application ..."
./gradlew :${template}:assembleDebug